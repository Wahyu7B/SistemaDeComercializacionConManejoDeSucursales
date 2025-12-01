package com.Proyecto_JS.ProyectoJS.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para procesar devoluciones de préstamos
 */
public class DevolucionDTO {

    @NotNull(message = "El ID del préstamo es obligatorio")
    private Long prestamoId;

    private String observaciones;
    private BigDecimal multaPagada;
    private Long adminDevolucionId;

    // Getters y Setters
    public Long getPrestamoId() { return prestamoId; }
    public void setPrestamoId(Long prestamoId) { this.prestamoId = prestamoId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public BigDecimal getMultaPagada() { return multaPagada; }
    public void setMultaPagada(BigDecimal multaPagada) { this.multaPagada = multaPagada; }

    public Long getAdminDevolucionId() { return adminDevolucionId; }
    public void setAdminDevolucionId(Long adminDevolucionId) { 
        this.adminDevolucionId = adminDevolucionId; 
    }
}
