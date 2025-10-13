// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/impl/NotificacionServiceImpl.java
package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private RestTemplate restTemplate;

    private final String NOTIFICACION_API_URL = "http://localhost:4000/api/enviar-confirmacion";
    private final String RECHAZO_API_URL = "http://localhost:4000/api/enviar-rechazo";


    @Override
    public void enviarConfirmacionDePedido(String emailCliente, Long numeroPedido) {
        try {
            Map<String, Object> requestBody = Map.of(
                "emailCliente", emailCliente,
                "numeroPedido", numeroPedido
            );
            
            System.out.println("Enviando petición a la API de Notificaciones...");
            restTemplate.postForObject(NOTIFICACION_API_URL, requestBody, String.class);
            System.out.println("Petición a la API de Notificaciones enviada con éxito.");

        } catch (Exception e) {
            System.err.println("Error al conectar con el servicio de notificaciones: " + e.getMessage());
        }
    }

    @Override
    public void enviarRechazoDePedido(String emailCliente, Long numeroPedido) {
        try {
            Map<String, Object> requestBody = Map.of("emailCliente", emailCliente, "numeroPedido", numeroPedido);
            System.out.println("Enviando petición de RECHAZO a la API de Notificaciones...");
            restTemplate.postForObject(RECHAZO_API_URL, requestBody, String.class);
            System.out.println("Petición de RECHAZO enviada con éxito.");
        } catch (Exception e) {
            System.err.println("Error al conectar con el servicio de notificaciones para rechazo: " + e.getMessage());
        }
    }
}