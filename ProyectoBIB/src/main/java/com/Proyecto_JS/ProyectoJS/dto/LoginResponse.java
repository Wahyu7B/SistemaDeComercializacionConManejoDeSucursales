package com.Proyecto_JS.ProyectoJS.dto;

/**
 * DTO para devolver el token JWT al cliente después de un login exitoso.
 * 
 * Se usa como respuesta del endpoint POST /api/auth/login
 * 
 * Ejemplo de JSON de respuesta:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 * }
 */
public class LoginResponse {
    
    private String token;

    // Constructor vacío
    public LoginResponse() {
    }

    // Constructor con token
    public LoginResponse(String token) {
        this.token = token;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
