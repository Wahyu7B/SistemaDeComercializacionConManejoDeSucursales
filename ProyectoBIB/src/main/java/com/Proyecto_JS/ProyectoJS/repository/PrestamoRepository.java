package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.Prestamo;
import com.Proyecto_JS.ProyectoJS.entity.Prestamo.EstadoPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // Buscar préstamos por usuario
    List<Prestamo> findByUsuarioIdOrderByFechaPrestamoDesc(Long usuarioId);

    // Buscar préstamos por estado
    List<Prestamo> findByEstadoOrderByFechaPrestamoDesc(EstadoPrestamo estado);

    // Buscar préstamos por usuario y estado
    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    List<Prestamo> findByUsuarioId(Long usuarioId);


    // Buscar préstamos activos de un usuario
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.estado = 'ACTIVO'")
    List<Prestamo> findPrestamosActivosByUsuario(@Param("usuarioId") Long usuarioId);

    // Contar préstamos activos de un usuario
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.estado = 'ACTIVO'")
    Long countPrestamosActivosByUsuario(@Param("usuarioId") Long usuarioId);

    // Buscar préstamos vencidos (fecha límite pasada y estado ACTIVO)
    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'ACTIVO' AND p.fechaDevolucionEsperada < :fecha")
    List<Prestamo> findPrestamosVencidos(@Param("fecha") LocalDateTime fecha);

    // Buscar préstamos por sucursal
    List<Prestamo> findBySucursalIdOrderByFechaPrestamoDesc(Long sucursalId);

    // Buscar préstamos por rango de fechas
    @Query("SELECT p FROM Prestamo p WHERE p.fechaPrestamo BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPrestamo DESC")
    List<Prestamo> findByFechaRange(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                     @Param("fechaFin") LocalDateTime fechaFin);
}
