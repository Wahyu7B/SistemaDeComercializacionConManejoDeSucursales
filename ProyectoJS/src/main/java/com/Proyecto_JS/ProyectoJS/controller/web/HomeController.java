// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/controller/web/HomeController.java
package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.entity.Sucursal; // Importar Sucursal
import com.Proyecto_JS.ProyectoJS.service.LibroService;
import com.Proyecto_JS.ProyectoJS.service.SucursalService; // Importar SucursalService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private LibroService libroService;


    @Autowired
    private SucursalService sucursalService;

    @GetMapping("/")
    public String mostrarPaginaDeInicio(Model model) {

        List<Libro> librosDestacados = libroService.obtenerTodosLosLibros().stream().limit(3).toList();
        

        List<Sucursal> sucursales = sucursalService.obtenerTodasLasSucursales();
        

        model.addAttribute("librosDestacados", librosDestacados);
        model.addAttribute("sucursales", sucursales);
        
        return "index"; 
    }
}