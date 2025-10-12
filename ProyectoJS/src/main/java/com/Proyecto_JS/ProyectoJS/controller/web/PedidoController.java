// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/controller/web/PedidoController.java
package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/checkout")
    public String mostrarCheckout(Model model) {
        return "public/checkout"; 
    }

    @PostMapping("/procesar")
    public String procesarPedido(@RequestParam Long carritoId,
                                 @RequestParam(required = false) Long direccionEnvioId,
                                 @RequestParam String tipoEntrega,
                                 @RequestParam(required = false) Long sucursalRecojoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(carritoId, direccionEnvioId, tipoEntrega, sucursalRecojoId);
            redirectAttributes.addFlashAttribute("successMessage", "¡Pedido #" + nuevoPedido.getId() + " creado con éxito!");
            return "redirect:/pedido/confirmacion/" + nuevoPedido.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar el pedido: " + e.getMessage());
            return "redirect:/pedido/checkout";
        }
    }
    
    @GetMapping("/confirmacion/{id}")
    public String mostrarConfirmacion(Model model) {
        return "public/confirmacion-pedido"; 
    }
}