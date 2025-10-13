package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Sucursal;
import com.Proyecto_JS.ProyectoJS.service.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @GetMapping("/sucursales")
    public String mostrarPaginaDeSucursales(Model model) {
        List<Sucursal> sucursales = sucursalService.obtenerTodasLasSucursales();
        model.addAttribute("sucursales", sucursales);
        return "public/sucursales"; 
    }
}