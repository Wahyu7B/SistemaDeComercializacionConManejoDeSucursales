package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Carrito;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("")
    public String verCarrito(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Carrito carrito = carritoService.obtenerCarritoDelUsuario(usuario.getId());
        model.addAttribute("carrito", carrito);

        return "public/carrito";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Long libroId,
                                   @RequestParam int cantidad,
                                   @RequestParam Long sucursalId,
                                   RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
            
            Carrito carrito = carritoService.obtenerCarritoDelUsuario(usuario.getId());
            
            carritoService.agregarLibroAlCarrito(carrito.getId(), libroId, cantidad, sucursalId);
            redirectAttributes.addFlashAttribute("successMessage", "Libro añadido al carrito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir el libro: " + e.getMessage());
        }
        return "redirect:/catalogo";
    }

    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam Long itemId, RedirectAttributes redirectAttributes) {
        try {
            carritoService.eliminarLibroDelCarrito(itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item eliminado del carrito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el item.");
        }
        return "redirect:/carrito";
    }
}