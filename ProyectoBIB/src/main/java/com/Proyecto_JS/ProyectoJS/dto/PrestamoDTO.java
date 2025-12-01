package com.Proyecto_JS.ProyectoJS.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para crear/actualizar préstamos
 */
public class PrestamoDTO {

    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

    @NotNull(message = "Debe incluir al menos un libro")
    @Size(min = 1, message = "Debe incluir al menos un libro")
    private List<DetalleLibroDTO> libros;

    private Integer diasPrestamo;
    private String observaciones;
    private LocalDateTime fechaDevolucionEsperada;
    private LocalDateTime fechaDevolucionReal;
    private String estado;
    private BigDecimal multa;

    // DTO interno para los libros del préstamo
    public static class DetalleLibroDTO {
        @NotNull(message = "El ID del libro es obligatorio")
        private Long libroId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;

        // Getters y Setters
        public Long getLibroId() { return libroId; }
        public void setLibroId(Long libroId) { this.libroId = libroId; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getSucursalId() { return sucursalId; }
    public void setSucursalId(Long sucursalId) { this.sucursalId = sucursalId; }

    public List<DetalleLibroDTO> getLibros() { return libros; }
    public void setLibros(List<DetalleLibroDTO> libros) { this.libros = libros; }

    public Integer getDiasPrestamo() { return diasPrestamo; }
    public void setDiasPrestamo(Integer diasPrestamo) { this.diasPrestamo = diasPrestamo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) { 
        this.fechaDevolucionEsperada = fechaDevolucionEsperada; 
    }

    public LocalDateTime getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDateTime fechaDevolucionReal) { 
        this.fechaDevolucionReal = fechaDevolucionReal; 
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getMulta() { return multa; }
    public void setMulta(BigDecimal multa) { this.multa = multa; }
}
