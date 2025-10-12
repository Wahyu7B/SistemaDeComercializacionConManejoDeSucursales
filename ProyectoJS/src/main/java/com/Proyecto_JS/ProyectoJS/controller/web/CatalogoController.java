// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/controller/web/CatalogoController.java
package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/catalogo") 
public class CatalogoController {

    @Autowired
    private LibroService libroService;


    @GetMapping("")
    public String mostrarCatalogo(Model model) {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        model.addAttribute("libros", libros); 
        return "public/catalogo"; 
    }


    @GetMapping("/libro/{id}")
    public String mostrarDetalleLibro(@PathVariable("id") Long id, Model model) {
        Libro libro = libroService.obtenerLibroPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con id: " + id));
        model.addAttribute("libro", libro);
        return "public/detalle-libro";
    }
    

    @GetMapping("/buscar")
    public String buscarLibros(@RequestParam("query") String query, Model model) {
        List<Libro> libros = libroService.buscarLibrosPorTitulo(query);
        model.addAttribute("libros", libros);
        model.addAttribute("query", query); 
        return "public/catalogo";
    }
}