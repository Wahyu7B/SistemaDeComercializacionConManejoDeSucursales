package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import java.util.List;

public interface PedidoService {

    Pedido crearPedido(Long carritoId, Long direccionEnvioId, String tipoEntrega, Long sucursalRecojoId);
    
    List<Pedido> obtenerPedidosPorUsuario(Usuario usuario);
    
}