// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/ProyectoJsApplication.java
package com.Proyecto_JS.ProyectoJS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Importar
import org.springframework.web.client.RestTemplate; // Importar

@SpringBootApplication
public class ProyectoJsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoJsApplication.class, args);
    }

    // ✅ AÑADE ESTE MÉTODO
    // Este Bean crea una única instancia de RestTemplate que podemos
    // inyectar y usar en cualquier servicio para hacer llamadas a APIs.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}