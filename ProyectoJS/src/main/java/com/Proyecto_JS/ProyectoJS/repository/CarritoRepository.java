package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, Carrito.EstadoCarrito estado);

}