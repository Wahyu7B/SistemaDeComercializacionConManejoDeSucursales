// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/controller/web/AuthController.java
package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.dto.UsuarioRegistroDTO;
import com.Proyecto_JS.ProyectoJS.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Asegúrate de importar Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormularioDeRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarCuentaDeUsuario(@ModelAttribute("usuario") UsuarioRegistroDTO registroDTO) {
        usuarioService.guardar(registroDTO);
        return "redirect:/registro?exito";
    }
    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }
}