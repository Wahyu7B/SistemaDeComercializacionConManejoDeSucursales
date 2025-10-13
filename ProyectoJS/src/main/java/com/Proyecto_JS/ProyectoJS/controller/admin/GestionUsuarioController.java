package com.Proyecto_JS.ProyectoJS.controller.admin;

import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class GestionUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("")
    public String mostrarGestionDeUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        return "admin/gestionar-usuarios";
    }

    @PostMapping("/cambiar-rol")
    public String cambiarRol(@RequestParam Long usuarioId, @RequestParam Usuario.Rol rol, RedirectAttributes attributes) {
        try {
            usuarioService.cambiarRol(usuarioId, rol);
            attributes.addFlashAttribute("successMessage", "Rol del usuario actualizado con éxito.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al cambiar el rol.");
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Long usuarioId, @RequestParam Usuario.EstadoUsuario estado, RedirectAttributes attributes) {
        try {
            usuarioService.cambiarEstado(usuarioId, estado);
            attributes.addFlashAttribute("successMessage", "Estado del usuario actualizado con éxito.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al cambiar el estado.");
        }
        return "redirect:/admin/usuarios";
    }
}