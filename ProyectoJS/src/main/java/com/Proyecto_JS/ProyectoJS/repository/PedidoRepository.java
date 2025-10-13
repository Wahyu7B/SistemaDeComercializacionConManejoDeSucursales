package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // ... dentro de la interfaz PedidoRepository

    List<Pedido> findByUsuario(Usuario usuario);

    // AÑADE ESTA LÍNEA
    List<Pedido> findByEstado(Pedido.EstadoPedido estado);
}