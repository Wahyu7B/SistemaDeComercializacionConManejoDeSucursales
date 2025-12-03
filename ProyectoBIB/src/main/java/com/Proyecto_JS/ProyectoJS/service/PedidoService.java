// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/PedidoService.java
package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface PedidoService {

    // ✅ Firma actualizada para recibir distrito y dirección
    Pedido crearPedido(Long carritoId, 
                       String tipoEntrega, 
                       Long sucursalRecojoId, 
                       String distrito, 
                       String direccion);
    
    List<Pedido> obtenerPedidosPorUsuario(Usuario usuario);
    void confirmarPedido(Long pedidoId);
    List<Pedido> obtenerPedidosPorEstado(Pedido.EstadoPedido estado);
    Optional<Pedido> obtenerPedidoPorId(Long id);
    void rechazarPedido(Long pedidoId);
}
