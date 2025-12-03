package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.Inventario;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.InventarioRepository;
import com.Proyecto_JS.ProyectoJS.service.InventarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Override
    public List<Inventario> obtenerTodoElInventario() {
        return inventarioRepository.findAll();
    }

    @Override
    public void actualizarStock(Long inventarioId, int nuevoStock) {
        Inventario inventario = inventarioRepository.findById(inventarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No se encontró el registro de inventario con ID: " + inventarioId));

        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        inventario.setStockVenta(nuevoStock);
        inventarioRepository.save(inventario);
        
        System.out.println("✅ Stock actualizado: " + inventario.getLibro().getTitulo() + " → " + nuevoStock);
    }

    // ==================== NUEVO MÉTODO PARA PAGINACIÓN ====================
    
    @Override
    public Page<Inventario> obtenerInventarioPaginado(Pageable pageable) {
        return inventarioRepository.findAll(pageable);
    }
}
