// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/impl/ReporteServiceImpl.java
package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.TopVendidoView;
import com.Proyecto_JS.ProyectoJS.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private RestTemplate restTemplate;

    private final String PDF_API_URL = "http://localhost:4001/api/generar-top-vendidos";

    @Override
    public byte[] generarTopVendidosPDF(List<TopVendidoView> topVendidos) {
        try {
            // Preparamos los encabezados para indicar que enviamos JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Creamos el cuerpo de la petición con los datos del reporte
            Map<String, List<TopVendidoView>> requestBody = Map.of("data", topVendidos);
            HttpEntity<Map<String, List<TopVendidoView>>> request = new HttpEntity<>(requestBody, headers);

            System.out.println("Enviando datos a la API de Node.js para generar PDF...");
            
            // Hacemos la petición POST y esperamos un array de bytes (el PDF)
            // Usamos postForObject y esperamos el tipo byte[]
            byte[] pdfBytes = restTemplate.postForObject(PDF_API_URL, request, byte[].class);
            
            System.out.println("PDF recibido con éxito desde el microservicio.");
            return pdfBytes;

        } catch (Exception e) {
            System.err.println("Error fatal al generar el PDF con Node.js: " + e.getMessage());
            // Devolvemos un array vacío para indicar que falló
            return new byte[0]; 
        }
    }
}