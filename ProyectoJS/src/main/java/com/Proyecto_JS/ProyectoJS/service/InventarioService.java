package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Inventario;
import java.util.List;

public interface InventarioService {
    List<Inventario> obtenerTodoElInventario();
    void actualizarStock(Long inventarioId, int nuevoStock);
}