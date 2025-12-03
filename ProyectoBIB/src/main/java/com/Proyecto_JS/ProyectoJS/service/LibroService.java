package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Libro;
import java.util.List;

public interface LibroService {

    List<Libro> obtenerTodosLosLibros();

    Libro obtenerLibroPorId(Long id);  // ✅ CAMBIADO: retorna Libro, no Optional

    Libro guardarLibro(Libro libro);

    void eliminarLibro(Long id);
    
    List<Libro> buscarLibrosPorTitulo(String titulo);
}
