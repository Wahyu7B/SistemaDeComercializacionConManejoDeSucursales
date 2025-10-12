package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {

    List<Libro> obtenerTodosLosLibros();

    Optional<Libro> obtenerLibroPorId(Long id);

    Libro guardarLibro(Libro libro);

    void eliminarLibro(Long id);
    
    List<Libro> buscarLibrosPorTitulo(String titulo);
}