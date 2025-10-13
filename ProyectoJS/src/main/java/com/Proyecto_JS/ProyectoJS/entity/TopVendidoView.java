// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/entity/TopVendidoView.java
package com.Proyecto_JS.ProyectoJS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable // Le dice a Hibernate que esta entidad es de solo lectura.
@Subselect("SELECT * FROM v_top_vendidos_90d") // Le dice a Hibernate que use la VISTA.
public class TopVendidoView {

    @Id
    private Long libro_id;
    private String titulo;
    private String autor;
    private Long unidades_vendidas;
    private Double ingreso_total;
    public Long getLibro_id() {
        return libro_id;
    }
    public void setLibro_id(Long libro_id) {
        this.libro_id = libro_id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public Long getUnidades_vendidas() {
        return unidades_vendidas;
    }
    public void setUnidades_vendidas(Long unidades_vendidas) {
        this.unidades_vendidas = unidades_vendidas;
    }
    public Double getIngreso_total() {
        return ingreso_total;
    }
    public void setIngreso_total(Double ingreso_total) {
        this.ingreso_total = ingreso_total;
    }

    // --- Getters y Setters ---
    // (Puedes generarlos automáticamente o escribirlos)
}