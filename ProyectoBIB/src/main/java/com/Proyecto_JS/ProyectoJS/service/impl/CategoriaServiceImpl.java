// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/impl/CategoriaServiceImpl.java
package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.Categoria;
import com.Proyecto_JS.ProyectoJS.repository.CategoriaRepository;
import com.Proyecto_JS.ProyectoJS.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }
}