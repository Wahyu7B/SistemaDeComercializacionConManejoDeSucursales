package com.Proyecto_JS.ProyectoJS.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la configuración global de préstamos del sistema.
 */
@Entity
@Table(name = "configuracion_prestamos")
public class ConfiguracionPrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    @Column(name = "dias_prestamo_default", nullable = false)
    private Integer diasPrestamoDefault = 15;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "multa_por_dia", nullable = false, precision = 10, scale = 2)
    private BigDecimal multaPorDia = new BigDecimal("2.00");

    @NotNull
    @Min(1)
    @Column(name = "max_prestamos_por_usuario", nullable = false)
    private Integer maxPrestamosPorUsuario = 3;

    @NotNull
    @Min(0)
    @Column(name = "max_renovaciones", nullable = false)
    private Integer maxRenovaciones = 1;

    @NotNull
    @Column(name = "permite_prestamos", nullable = false)
    private Boolean permitePrestamos = true;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getDiasPrestamoDefault() { return diasPrestamoDefault; }
    public void setDiasPrestamoDefault(Integer diasPrestamoDefault) { 
        this.diasPrestamoDefault = diasPrestamoDefault; 
    }

    public BigDecimal getMultaPorDia() { return multaPorDia; }
    public void setMultaPorDia(BigDecimal multaPorDia) { this.multaPorDia = multaPorDia; }

    public Integer getMaxPrestamosPorUsuario() { return maxPrestamosPorUsuario; }
    public void setMaxPrestamosPorUsuario(Integer maxPrestamosPorUsuario) { 
        this.maxPrestamosPorUsuario = maxPrestamosPorUsuario; 
    }

    public Integer getMaxRenovaciones() { return maxRenovaciones; }
    public void setMaxRenovaciones(Integer maxRenovaciones) { 
        this.maxRenovaciones = maxRenovaciones; 
    }

    public Boolean getPermitePrestamos() { return permitePrestamos; }
    public void setPermitePrestamos(Boolean permitePrestamos) { 
        this.permitePrestamos = permitePrestamos; 
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
