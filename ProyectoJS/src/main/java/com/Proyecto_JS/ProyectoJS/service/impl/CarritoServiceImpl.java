package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.Carrito;
import com.Proyecto_JS.ProyectoJS.entity.CarritoItem;
import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.entity.Sucursal;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.*;
import com.Proyecto_JS.ProyectoJS.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private SucursalRepository sucursalRepository;
    @Autowired
    private CarritoItemRepository carritoItemRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; 

    @Override
    @Transactional
    public Carrito obtenerCarritoDelUsuario(Long usuarioId) {
        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, Carrito.EstadoCarrito.ABIERTO)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado")));
                    nuevoCarrito.setEstado(Carrito.EstadoCarrito.ABIERTO);
                    nuevoCarrito.setCreatedAt(LocalDateTime.now());
                    nuevoCarrito.setUpdatedAt(LocalDateTime.now());
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    @Override
    @Transactional
    public void agregarLibroAlCarrito(Long carritoId, Long libroId, int cantidad, Long sucursalId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado"));
        Libro libro = libroRepository.findById(libroId).orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado"));
        Sucursal sucursal = sucursalRepository.findById(sucursalId).orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada"));


        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setLibro(libro);
        item.setSucursal(sucursal);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(libro.getPrecioVenta());
        item.setCreatedAt(LocalDateTime.now());

        carritoItemRepository.save(item);
        
        carrito.setUpdatedAt(LocalDateTime.now());
        carritoRepository.save(carrito);
    }

    @Override
    @Transactional
    public void eliminarLibroDelCarrito(Long carritoItemId) {
        CarritoItem item = carritoItemRepository.findById(carritoItemId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Item del carrito no encontrado"));
        
        carritoItemRepository.delete(item);

        Carrito carrito = item.getCarrito();
        carrito.setUpdatedAt(LocalDateTime.now());
        carritoRepository.save(carrito);
    }

    @Override
    @Transactional
    public void actualizarCantidad(Long carritoItemId, int cantidad) {
        CarritoItem item = carritoItemRepository.findById(carritoItemId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Item del carrito no encontrado"));
        
        if (cantidad <= 0) {
            eliminarLibroDelCarrito(carritoItemId);
        } else {
            item.setCantidad(cantidad);
            carritoItemRepository.save(item);

            Carrito carrito = item.getCarrito();
            carrito.setUpdatedAt(LocalDateTime.now());
            carritoRepository.save(carrito);
        }
    }
}