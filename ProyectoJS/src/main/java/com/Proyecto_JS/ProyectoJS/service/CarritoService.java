package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Carrito;

public interface CarritoService {

    Carrito obtenerCarritoDelUsuario(Long usuarioId); 

    void agregarLibroAlCarrito(Long carritoId, Long libroId, int cantidad, Long sucursalId);

    void eliminarLibroDelCarrito(Long carritoItemId);

    void actualizarCantidad(Long carritoItemId, int cantidad);
}