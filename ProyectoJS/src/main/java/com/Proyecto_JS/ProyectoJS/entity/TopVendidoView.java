
package com.Proyecto_JS.ProyectoJS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Data
public class TopVendidoView {
    @Id
    private Long id;
    private String nombre;
    private Long totalVendido;
}