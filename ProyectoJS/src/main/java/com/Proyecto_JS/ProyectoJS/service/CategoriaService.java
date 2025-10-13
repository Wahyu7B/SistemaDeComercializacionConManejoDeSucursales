// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/CategoriaService.java
package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> obtenerTodasLasCategorias();
}