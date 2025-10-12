package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
}