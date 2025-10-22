package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    Page<Libro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    
    Page<Libro> findByCategoriaId(Long categoriaId, Pageable pageable);

    Page<Libro> findByTituloContainingIgnoreCaseAndCategoriaId(String titulo, Long categoriaId, Pageable pageable);
    
    List<Libro> findByOrderByCreatedAtDesc(Pageable pageable);
}