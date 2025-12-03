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
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);

        // métricas simples usando la lista
        long totalUsuarios = usuarios.size();
        long totalActivos = usuarios.stream()
                .filter(u -> u.getEstado() == Usuario.EstadoUsuario.ACTIVO)
                .count();
        long totalAdmins = usuarios.stream()
                .filter(u -> u.getRol() == Usuario.Rol.ADMIN)
                .count();

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalActivos", totalActivos);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("nuevosHoy", 0); // si aún no tienes fechaRegistro, lo dejas en 0

        // para que la vista de usuarios no falle con paginación
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalItems", totalUsuarios);

        return "admin/gestionar-usuarios";
    }

    @PostMapping("/cambiar-rol")
    public String cambiarRol(@RequestParam Long usuarioId,
                             @RequestParam Usuario.Rol rol,
                             RedirectAttributes attributes) {
        try {
            usuarioService.cambiarRol(usuarioId, rol);
            attributes.addFlashAttribute("successMessage", "Rol del usuario actualizado con éxito.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al cambiar el rol: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Long usuarioId,
                                @RequestParam Usuario.EstadoUsuario estado,
                                RedirectAttributes attributes) {
        try {
            usuarioService.cambiarEstado(usuarioId, estado);
            attributes.addFlashAttribute("successMessage", "Estado del usuario actualizado con éxito.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al cambiar el estado: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam Long usuarioId,
                                  RedirectAttributes attributes) {
        try {
            usuarioService.eliminarUsuario(usuarioId);
            attributes.addFlashAttribute("successMessage", "Usuario eliminado con éxito.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
