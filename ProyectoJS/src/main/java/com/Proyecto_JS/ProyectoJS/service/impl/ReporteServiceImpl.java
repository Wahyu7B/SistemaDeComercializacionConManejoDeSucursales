package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.TopVendidoView;
import com.Proyecto_JS.ProyectoJS.service.ReporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] generarTopVendidosPDF(List<TopVendidoView> topVendidos) {
        // ✅ CORRECCIÓN: Apuntar al puerto 4001 y a la ruta '/reporte-libros-vendidos'
        String url = "http://localhost:4001/reporte-libros-vendidos";

        try {
            System.out.println("Enviando datos a la API de Node.js para generar PDF...");

            // Preparar el cuerpo de la petición
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("libros", topVendidos);
            requestBody.put("fecha", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Configurar cabeceras
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crear la petición
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            // Realizar la llamada POST y obtener la respuesta
            ResponseEntity<byte[]> response = restTemplate.postForEntity(url, request, byte[].class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("PDF recibido exitosamente desde Node.js.");
                return response.getBody();
            } else {
                System.err.println("Error al generar PDF. Código de estado: " + response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error fatal al generar el PDF con Node.js: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}