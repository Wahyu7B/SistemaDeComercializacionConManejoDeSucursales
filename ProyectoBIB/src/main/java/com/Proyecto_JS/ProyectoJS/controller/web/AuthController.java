package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.dto.UsuarioRegistroDTO;
import com.Proyecto_JS.ProyectoJS.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;

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
    public String registrarCuentaDeUsuario(
            @Valid @ModelAttribute("usuario") UsuarioRegistroDTO registroDTO,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            return "auth/registro";
        }
        
        usuarioService.guardar(registroDTO);
        return "redirect:/registro?exito";
    }
    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }
}
