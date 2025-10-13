// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/entity/Inventario.java
package com.Proyecto_JS.ProyectoJS.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventarios")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ LÍNEA CORREGIDA: Cambiado a FetchType.EAGER
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    // ✅ LÍNEA CORREGIDA: Cambiado a FetchType.EAGER
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(name = "stock_venta", nullable = false)
    private int stockVenta;

    @Column(nullable = false)
    private boolean activo;

    // --- Getters y Setters (se quedan igual) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }
    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
    public int getStockVenta() { return stockVenta; }
    public void setStockVenta(int stockVenta) { this.stockVenta = stockVenta; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}