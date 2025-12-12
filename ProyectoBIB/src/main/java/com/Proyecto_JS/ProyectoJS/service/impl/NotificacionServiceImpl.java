package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import jakarta.annotation.PostConstruct;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private RestTemplate restTemplate;

    // ✅ Detección automática de entorno
    private final String notificacionesBaseUrl = 
        System.getenv("RAILWAY_ENVIRONMENT") != null 
            ? "https://servicio-notificaciones-production.up.railway.app"
            : "http://localhost:4000";

    @PostConstruct
    public void init() {
        System.out.println("════════════════════════════════════════");
        System.out.println("🔧 NOTIFICACIONES_URL configurada: " + notificacionesBaseUrl);
        System.out.println("🌍 Entorno: " + (System.getenv("RAILWAY_ENVIRONMENT") != null ? "RAILWAY" : "LOCAL"));
        System.out.println("════════════════════════════════════════");
    }

    @Override
    public void enviarConfirmacionDePedido(String emailCliente, Long numeroPedido) {
        try {
            String url = notificacionesBaseUrl + "/api/enviar-confirmacion";
            
            Map<String, Object> requestBody = Map.of(
                "emailCliente", emailCliente,
                "numeroPedido", numeroPedido
            );
            
            System.out.println("🔍 URL del servicio: " + url);
            System.out.println("📧 Enviando petición a la API de Notificaciones...");
            
            restTemplate.postForObject(url, requestBody, String.class);
            
            System.out.println("✅ Petición a la API de Notificaciones enviada con éxito.");

        } catch (Exception e) {
            System.err.println("❌ Error al conectar con el servicio de notificaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void enviarRechazoDePedido(String emailCliente, Long numeroPedido) {
        try {
            String url = notificacionesBaseUrl + "/api/enviar-rechazo";
            
            Map<String, Object> requestBody = Map.of(
                "emailCliente", emailCliente, 
                "numeroPedido", numeroPedido
            );
            
            System.out.println("🔍 URL del servicio: " + url);
            System.out.println("📧 Enviando petición de RECHAZO a la API de Notificaciones...");
            
            restTemplate.postForObject(url, requestBody, String.class);
            
            System.out.println("✅ Petición de RECHAZO enviada con éxito.");
            
        } catch (Exception e) {
            System.err.println("❌ Error al conectar con el servicio de notificaciones para rechazo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
