package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.*;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.*;
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
    private PedidoDetalleRepository pedidoDetalleRepository;

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
        pedido.setEstado(Pedido.EstadoPedido.CREADO);
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

    private void actualizarStock(CarritoItem item) {
        Inventario inventario = inventarioRepository.findBySucursalAndLibro(item.getSucursal(), item.getLibro())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró inventario para el libro " + item.getLibro().getTitulo() + " en la sucursal " + item.getSucursal().getNombre()));
        
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
}