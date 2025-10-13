// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/ReporteService.java
package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.entity.TopVendidoView;
import java.util.List;

public interface ReporteService {
    byte[] generarTopVendidosPDF(List<TopVendidoView> topVendidos);
}