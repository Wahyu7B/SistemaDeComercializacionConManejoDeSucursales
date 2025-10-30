package com.Proyecto_JS.ProyectoJS.config;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

  private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

  @PostConstruct
  public void init() {
    log.info("Aplicación iniciada y logging configurado correctamente");
  }
}

