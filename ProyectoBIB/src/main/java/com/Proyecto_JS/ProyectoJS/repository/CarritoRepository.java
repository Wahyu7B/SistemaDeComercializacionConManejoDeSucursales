package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, Carrito.EstadoCarrito estado);

    @Query("SELECT DISTINCT c FROM Carrito c " +
           "LEFT JOIN FETCH c.items i " +
           "LEFT JOIN FETCH i.libro " +
           "LEFT JOIN FETCH i.sucursal " +
           "WHERE c.usuario.id = :usuarioId " +
           "AND c.estado = 'ABIERTO'")
    Optional<Carrito> findByUsuarioIdWithItemsAndLibros(@Param("usuarioId") Long usuarioId);

}