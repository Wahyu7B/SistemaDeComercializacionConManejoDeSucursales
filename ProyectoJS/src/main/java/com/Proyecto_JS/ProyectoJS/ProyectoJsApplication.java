package com.Proyecto_JS.ProyectoJS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; 
import org.springframework.web.client.RestTemplate; 

@SpringBootApplication
public class ProyectoJsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoJsApplication.class, args);
    }
    //Lamado de Apis
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}