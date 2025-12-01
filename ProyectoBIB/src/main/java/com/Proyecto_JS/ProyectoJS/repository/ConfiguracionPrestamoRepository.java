package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.ConfiguracionPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionPrestamoRepository extends JpaRepository<ConfiguracionPrestamo, Long> {
    
    // No necesita métodos adicionales por ahora, solo usaremos findById(1L)
}
