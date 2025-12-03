package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Inventario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventarioService {
    List<Inventario> obtenerTodoElInventario();
    void actualizarStock(Long inventarioId, int nuevoStock);
    Page<Inventario> obtenerInventarioPaginado(Pageable pageable);
}