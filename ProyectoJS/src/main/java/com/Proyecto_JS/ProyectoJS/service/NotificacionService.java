// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/NotificacionService.java
package com.Proyecto_JS.ProyectoJS.service;

public interface NotificacionService {
    void enviarConfirmacionDePedido(String emailCliente, Long numeroPedido);
    void enviarRechazoDePedido(String emailCliente, Long numeroPedido);
}