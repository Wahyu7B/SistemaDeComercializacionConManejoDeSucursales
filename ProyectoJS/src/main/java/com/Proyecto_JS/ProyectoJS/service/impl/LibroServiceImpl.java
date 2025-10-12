package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.repository.LibroRepository;
import com.Proyecto_JS.ProyectoJS.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class LibroServiceImpl implements LibroService {

    @Autowired 
    private LibroRepository libroRepository;

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        return libroRepository.findAll();
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    @Override
    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }

    @Override
    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }
}