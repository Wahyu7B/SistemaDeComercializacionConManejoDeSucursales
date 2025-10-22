// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/impl/PedidoServiceImpl.java
package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.*;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.*;
import com.Proyecto_JS.ProyectoJS.service.NotificacionService; // ✅ 1. IMPORTAR NUEVO SERVICIO
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private DireccionEnvioRepository direccionEnvioRepository;
    @Autowired
    private SucursalRepository sucursalRepository;
    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private NotificacionService notificacionService;

    // Tu método crearPedido se queda exactamente igual
    @Override
    @Transactional
    public Pedido crearPedido(Long carritoId, Long direccionEnvioId, String tipoEntrega, Long sucursalRecojoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado con id: " + carritoId));

        if (carrito.getItems().isEmpty() || carrito.getEstado() != Carrito.EstadoCarrito.ABIERTO) {
            throw new IllegalStateException("El carrito está vacío o ya ha sido procesado.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(carrito.getUsuario());
        pedido.setCarrito(carrito);
        pedido.setMoneda("PEN");
        // Cambiamos el estado inicial para reflejar que necesita revisión
        pedido.setEstado(Pedido.EstadoPedido.PAGO_EN_REVISION); 
        pedido.setCreatedAt(LocalDateTime.now());
        pedido.setUpdatedAt(LocalDateTime.now());

        Pedido.TipoEntrega entrega = Pedido.TipoEntrega.valueOf(tipoEntrega.toUpperCase());
        pedido.setTipoEntrega(entrega);

        if (entrega == Pedido.TipoEntrega.DELIVERY) {
            DireccionEnvio direccion = direccionEnvioRepository.findById(direccionEnvioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dirección de envío no encontrada con id: " + direccionEnvioId));
            pedido.setDireccionEnvio(direccion);
        } else if (entrega == Pedido.TipoEntrega.RECOJO_TIENDA) {
            Sucursal sucursal = sucursalRepository.findById(sucursalRecojoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada con id: " + sucursalRecojoId));
            pedido.setSucursalRecojo(sucursal);
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

        pedido.setTotal(totalPedido);
        pedido.setDetalles(detalles);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        carrito.setEstado(Carrito.EstadoCarrito.COMPRADO);
        carritoRepository.save(carrito);

        return pedidoGuardado;
    }

    // Tu método actualizarStock se queda igual
    private void actualizarStock(CarritoItem item) {
        Inventario inventario = inventarioRepository.findBySucursalAndLibro(item.getSucursal(), item.getLibro())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró inventario para el libro " + item.getLibro().getTitulo() + " en la sucursal " + item.getSucursal().getNombre()));
        
        if (inventario.getStockVenta() < item.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para el libro: " + item.getLibro().getTitulo());
        }

        inventario.setStockVenta(inventario.getStockVenta() - item.getCantidad());
        inventarioRepository.save(inventario);
    }

    // Tu método obtenerPedidosPorUsuario se queda igual
    @Override
    public List<Pedido> obtenerPedidosPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    // ✅ 3. IMPLEMENTACIÓN DE LOS NUEVOS MÉTODOS
    @Override
    @Transactional
    public void confirmarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));

        pedido.setEstado(Pedido.EstadoPedido.PAGADO);
        pedido.setUpdatedAt(LocalDateTime.now());
        pedidoRepository.save(pedido);

        // ¡Aquí es donde llamamos al microservicio de Node.js!
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
        // 1. Buscamos el pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));

        // 2. Cambiamos su estado a ANULADO
        pedido.setEstado(Pedido.EstadoPedido.ANULADO);
        pedido.setUpdatedAt(LocalDateTime.now());
        
        // 3. LÓGICA CLAVE: Devolvemos el stock al inventario
        for (PedidoDetalle detalle : pedido.getDetalles()) {
            Inventario inventario = inventarioRepository.findBySucursalAndLibro(detalle.getSucursalOrigen(), detalle.getLibro())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró inventario para el libro " + detalle.getLibro().getTitulo()));
            
            // Sumamos la cantidad del pedido de vuelta al stock de venta
            inventario.setStockVenta(inventario.getStockVenta() + detalle.getCantidad());
            inventarioRepository.save(inventario);
        }

        pedidoRepository.save(pedido);
            notificacionService.enviarRechazoDePedido(
            pedido.getUsuario().getEmail(),
            pedido.getId()
        );
        
        // Opcional: Podríamos llamar a NotificacionService para enviar un correo de "Pedido Rechazado".
    }
}