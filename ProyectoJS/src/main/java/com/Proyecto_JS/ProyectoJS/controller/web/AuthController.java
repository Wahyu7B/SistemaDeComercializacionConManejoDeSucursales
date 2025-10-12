package com.Proyecto_JS.ProyectoJS.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {

        return "auth/registro";
    }
}