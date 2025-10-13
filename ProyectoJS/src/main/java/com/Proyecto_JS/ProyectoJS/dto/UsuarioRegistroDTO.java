// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/dto/UsuarioRegistroDTO.java
package com.Proyecto_JS.ProyectoJS.dto;

public class UsuarioRegistroDTO {
    
    private String nombreCompleto;
    private String email;
    private String password;

    // --- MÉTODOS GETTERS Y SETTERS ESCRITOS MANUALMENTE ---

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}