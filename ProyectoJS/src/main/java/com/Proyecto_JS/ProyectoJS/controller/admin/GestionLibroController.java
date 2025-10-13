package com.Proyecto_JS.ProyectoJS.controller.admin;

import com.Proyecto_JS.ProyectoJS.entity.Categoria;
import com.Proyecto_JS.ProyectoJS.entity.Inventario;
import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.service.CategoriaService;
import com.Proyecto_JS.ProyectoJS.service.InventarioService;
import com.Proyecto_JS.ProyectoJS.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/libros")
public class GestionLibroController {

    @Autowired private LibroService libroService;
    @Autowired private InventarioService inventarioService;
    @Autowired private CategoriaService categoriaService;

    @GetMapping("")
    public String mostrarInventario(Model model) {
        model.addAttribute("inventario", inventarioService.obtenerTodoElInventario());
        model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        model.addAttribute("libroNuevo", new Libro()); // Para el formulario del modal
        return "admin/gestionar-libros";
    }
    
    @PostMapping("/guardar")
    public String guardarLibro(@ModelAttribute("libroNuevo") Libro libro, RedirectAttributes attributes) {
        try {
            libro.setEstado(Libro.EstadoLibro.ACTIVO);
            libro.setCreatedAt(java.time.LocalDateTime.now());
            libro.setUpdatedAt(java.time.LocalDateTime.now());
            libroService.guardarLibro(libro);
            attributes.addFlashAttribute("successMessage", "Libro agregado con éxito.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al agregar el libro: " + e.getMessage());
        }
        return "redirect:/admin/libros";
    }

    @PostMapping("/actualizar-stock")
    public String actualizarStock(@RequestParam Long inventarioId, @RequestParam int nuevoStock, RedirectAttributes attributes) {
        try {
            inventarioService.actualizarStock(inventarioId, nuevoStock);
            attributes.addFlashAttribute("successMessage", "Stock actualizado correctamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al actualizar el stock.");
        }
        return "redirect:/admin/libros";
    }
}