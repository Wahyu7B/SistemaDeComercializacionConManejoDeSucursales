package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.PrestamoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoDetalleRepository extends JpaRepository<PrestamoDetalle, Long> {

    // Buscar detalles por préstamo
    List<PrestamoDetalle> findByPrestamoId(Long prestamoId);

    // Buscar detalles por libro
    List<PrestamoDetalle> findByLibroId(Long libroId);
}
