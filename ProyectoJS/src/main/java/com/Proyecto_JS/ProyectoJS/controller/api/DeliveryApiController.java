package com.Proyecto_JS.ProyectoJS.controller.api;

import com.Proyecto_JS.ProyectoJS.service.GeolocalizacionService; // ✅ ESTA LÍNEA ES CRUCIAL
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryApiController {

    @Autowired
    private GeolocalizacionService geolocalizacionService;

    @GetMapping("/costo")
    public BigDecimal obtenerCostoEnvio(@RequestParam String distrito) {
        return geolocalizacionService.calcularCostoEnvio(distrito);
    }
}