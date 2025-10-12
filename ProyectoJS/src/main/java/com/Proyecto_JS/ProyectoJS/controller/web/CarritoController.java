package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("")
    public String verCarrito(Model model) {
        return "public/carrito"; 
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Long libroId,
                                   @RequestParam int cantidad,
                                   @RequestParam Long sucursalId,
                                   RedirectAttributes redirectAttributes) {
        try {
            Long carritoId = 1L;
            carritoService.agregarLibroAlCarrito(carritoId, libroId, cantidad, sucursalId);
            redirectAttributes.addFlashAttribute("successMessage", "Libro añadido al carrito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir el libro: " + e.getMessage());
        }
        return "redirect:/catalogo/libro/" + libroId;
    }
    

    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam Long itemId, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("successMessage", "Item eliminado del carrito.");
        return "redirect:/carrito";
    }
}