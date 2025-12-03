package com.Proyecto_JS.ProyectoJS.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un préstamo de libros.
 * Un préstamo puede contener múltiples libros (relación con PrestamoDetalle).
 */
@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @NotNull
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDateTime fechaPrestamo;

    @NotNull
    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDateTime fechaDevolucionEsperada;

    @Column(name = "fecha_devolucion_real")
    private LocalDateTime fechaDevolucionReal;

    @NotNull
    @Min(1)
    @Column(name = "dias_prestamo", nullable = false)
    private Integer diasPrestamo = 15;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal multa = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_prestamo_id")
    private Usuario adminPrestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_devolucion_id")
    private Usuario adminDevolucion;

    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PrestamoDetalle> detalles = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;

    @Column(name = "comentarios", length = 500)
    private String comentarios;

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, VENCIDO, RENOVADO
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaPrestamo == null) {
            fechaPrestamo = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public void addDetalle(PrestamoDetalle detalle) {
        detalles.add(detalle);
        detalle.setPrestamo(this);
    }

    public void removeDetalle(PrestamoDetalle detalle) {
        detalles.remove(detalle);
        detalle.setPrestamo(null);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }

    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) { 
        this.fechaDevolucionEsperada = fechaDevolucionEsperada; 
    }

    public LocalDateTime getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDateTime fechaDevolucionReal) { 
        this.fechaDevolucionReal = fechaDevolucionReal; 
    }

    public Integer getDiasPrestamo() { return diasPrestamo; }
    public void setDiasPrestamo(Integer diasPrestamo) { this.diasPrestamo = diasPrestamo; }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    public BigDecimal getMulta() { return multa; }
    public void setMulta(BigDecimal multa) { this.multa = multa; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Usuario getAdminPrestamo() { return adminPrestamo; }
    public void setAdminPrestamo(Usuario adminPrestamo) { this.adminPrestamo = adminPrestamo; }

    public Usuario getAdminDevolucion() { return adminDevolucion; }
    public void setAdminDevolucion(Usuario adminDevolucion) { this.adminDevolucion = adminDevolucion; }

    public List<PrestamoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<PrestamoDetalle> detalles) { this.detalles = detalles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
