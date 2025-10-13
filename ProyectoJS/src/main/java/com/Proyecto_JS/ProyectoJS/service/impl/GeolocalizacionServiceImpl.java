// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/impl/GeolocalizacionServiceImpl.java
package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.service.GeolocalizacionService;
import org.springframework.stereotype.Service; // Importar la etiqueta Service
import java.math.BigDecimal;
import java.util.Map;

@Service // ✅ ESTA ETIQUETA HACE QUE SPRING LO RECONOZCA
public class GeolocalizacionServiceImpl implements GeolocalizacionService {

    // Simulación de la tabla de costos (el cuerpo de tu API)
    private static final Map<String, BigDecimal> COSTOS_POR_DISTRITO = Map.of(
        "Chiclayo", new BigDecimal("10.00"),
        "La Victoria", new BigDecimal("12.00"),
        "José Leonardo Ortiz", new BigDecimal("15.00")
    );

    @Override
    public BigDecimal calcularCostoEnvio(String distrito) {
        return COSTOS_POR_DISTRITO.getOrDefault(distrito, new BigDecimal("0.00"));
    }
}