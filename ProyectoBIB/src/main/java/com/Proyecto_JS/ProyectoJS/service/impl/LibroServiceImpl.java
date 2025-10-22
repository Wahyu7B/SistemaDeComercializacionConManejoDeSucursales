package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.Inventario;
import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.entity.Sucursal;
import com.Proyecto_JS.ProyectoJS.repository.InventarioRepository;
import com.Proyecto_JS.ProyectoJS.repository.LibroRepository;
import com.Proyecto_JS.ProyectoJS.repository.SucursalRepository;
import com.Proyecto_JS.ProyectoJS.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private SucursalRepository sucursalRepository;
    
    @Autowired
    private InventarioRepository inventarioRepository;

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        return libroRepository.findAll();
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    @Override
    @Transactional
    public Libro guardarLibro(Libro libro) {
        // Primero, guardamos el nuevo libro para que tenga un ID asignado por la base de datos.
        Libro libroGuardado = libroRepository.save(libro);
        
        // Luego, obtenemos la lista de todas las sucursales que existen en el sistema.
        List<Sucursal> sucursales = sucursalRepository.findAll();
        
        // Para cada sucursal encontrada, creamos un nuevo registro de inventario para el libro recién creado.
        for (Sucursal sucursal : sucursales) {
            Inventario nuevoInventario = new Inventario();
            nuevoInventario.setLibro(libroGuardado);
            nuevoInventario.setSucursal(sucursal);
            nuevoInventario.setStockVenta(0); // El stock inicial para un libro nuevo siempre será 0.
            nuevoInventario.setActivo(true);
            
            // Guardamos el nuevo registro de inventario en la base de datos.
            inventarioRepository.save(nuevoInventario);
        }
        
        return libroGuardado;
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