// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/controller/admin/AdminDashboardController.java
package com.Proyecto_JS.ProyectoJS.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Todas las URLs de esta clase empezarán con /admin
public class AdminDashboardController {

    @GetMapping("/dashboard")
    public String mostrarDashboard() {
        // Podríamos añadir lógica para pasar datos a la vista, como estadísticas
        return "admin/dashboard"; // Apunta al nuevo archivo HTML
    }
}