package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.*;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.*;
import com.Proyecto_JS.ProyectoJS.service.NotificacionService;
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private SucursalRepository sucursalRepository;
    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private NotificacionService notificacionService;

    @Override
    @Transactional
    public Pedido crearPedido(Long carritoId,
                              String tipoEntrega,
                              Long sucursalRecojoId,
                              String distrito,
                              String direccion) {

        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado con id: " + carritoId));

        if (carrito.getItems().isEmpty() || carrito.getEstado() != Carrito.EstadoCarrito.ABIERTO) {
            throw new IllegalStateException("El carrito está vacío o ya ha sido procesado.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(carrito.getUsuario());
        pedido.setCarrito(carrito);
        pedido.setMoneda("PEN");
        pedido.setEstado(Pedido.EstadoPedido.PAGO_EN_REVISION);
        pedido.setCreatedAt(LocalDateTime.now());
        pedido.setUpdatedAt(LocalDateTime.now());

        Pedido.TipoEntrega entrega = Pedido.TipoEntrega.valueOf(tipoEntrega.toUpperCase());
        pedido.setTipoEntrega(entrega);

        // Ajustar campos según tipo de entrega (para cumplir chk_entrega_coherente)
        if (entrega == Pedido.TipoEntrega.DELIVERY) {
            // No sucursal, ni direccionEnvio antigua
            pedido.setSucursalRecojo(null);
            pedido.setDireccionEnvio(null);

            // Datos de delivery
            pedido.setDistritoEntrega(distrito);
            pedido.setDireccionEntrega(direccion);

            BigDecimal costoEnvio = calcularCostoEnvioPorDistrito(distrito);
            pedido.setCostoEnvio(costoEnvio);

        } else if (entrega == Pedido.TipoEntrega.RECOJO_TIENDA) {
            // Sin datos de delivery
            pedido.setDistritoEntrega(null);
            pedido.setDireccionEntrega(null);

            // Sin direccionEnvio antigua
            pedido.setDireccionEnvio(null);

            Sucursal sucursal = sucursalRepository.findById(sucursalRecojoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada con id: " + sucursalRecojoId));
            pedido.setSucursalRecojo(sucursal);

            pedido.setCostoEnvio(BigDecimal.ZERO);
        }

        Set<PedidoDetalle> detalles = new HashSet<>();
        BigDecimal totalPedido = BigDecimal.ZERO;

        for (CarritoItem item : carrito.getItems()) {
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setLibro(item.getLibro());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSucursalOrigen(item.getSucursal());

            detalles.add(detalle);
            totalPedido = totalPedido.add(item.getSubtotal());

            actualizarStock(item);
        }

        // Total = subtotal + costo de envío
        totalPedido = totalPedido.add(pedido.getCostoEnvio() != null ? pedido.getCostoEnvio() : BigDecimal.ZERO);
        pedido.setTotal(totalPedido);
        pedido.setDetalles(detalles);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        carrito.setEstado(Carrito.EstadoCarrito.COMPRADO);
        carritoRepository.save(carrito);

        return pedidoGuardado;
    }

    private BigDecimal calcularCostoEnvioPorDistrito(String distrito) {
        Map<String, Double> costos = new HashMap<>();
        costos.put("La Victoria", 12.00);
        costos.put("San Isidro", 15.00);
        costos.put("Miraflores", 15.00);
        costos.put("Surco", 18.00);
        costos.put("Callao", 20.00);
        costos.put("San Juan de Lurigancho", 25.00);
        costos.put("Ate", 22.00);
        costos.put("Breña", 13.00);
        costos.put("Lince", 13.00);
        costos.put("Pueblo Libre", 14.00);

        return BigDecimal.valueOf(costos.getOrDefault(distrito, 0.0));
    }

    private void actualizarStock(CarritoItem item) {
        Inventario inventario = inventarioRepository
                .findBySucursalAndLibro(item.getSucursal(), item.getLibro())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró inventario para el libro " + item.getLibro().getTitulo()
                                + " en la sucursal " + item.getSucursal().getNombre()));

        if (inventario.getStockVenta() < item.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para el libro: " + item.getLibro().getTitulo());
        }

        inventario.setStockVenta(inventario.getStockVenta() - item.getCantidad());
        inventarioRepository.save(inventario);
    }

    @Override
    public List<Pedido> obtenerPedidosPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public void confirmarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));

        pedido.setEstado(Pedido.EstadoPedido.PAGADO);
        pedido.setUpdatedAt(LocalDateTime.now());
        pedidoRepository.save(pedido);

        notificacionService.enviarConfirmacionDePedido(
                pedido.getUsuario().getEmail(),
                pedido.getId()
        );
    }

    @Override
    public List<Pedido> obtenerPedidosPorEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @Override
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public void rechazarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));

        pedido.setEstado(Pedido.EstadoPedido.ANULADO);
        pedido.setUpdatedAt(LocalDateTime.now());

        for (PedidoDetalle detalle : pedido.getDetalles()) {
            Inventario inventario = inventarioRepository
                    .findBySucursalAndLibro(detalle.getSucursalOrigen(), detalle.getLibro())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró inventario para el libro " + detalle.getLibro().getTitulo()));

            inventario.setStockVenta(inventario.getStockVenta() + detalle.getCantidad());
            inventarioRepository.save(inventario);
        }

        pedidoRepository.save(pedido);

        notificacionService.enviarRechazoDePedido(
                pedido.getUsuario().getEmail(),
                pedido.getId()
        );
    }
}
