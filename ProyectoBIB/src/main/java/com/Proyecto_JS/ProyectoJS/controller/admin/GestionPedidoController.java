package com.Proyecto_JS.ProyectoJS.controller.admin;

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

import java.util.List;

@Controller
@RequestMapping("/admin/pedidos")
public class GestionPedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("")
    public String mostrarPedidosParaGestionar(Model model) {
        List<Pedido> pedidosPendientes = pedidoService.obtenerPedidosPorEstado(Pedido.EstadoPedido.PAGO_EN_REVISION);
        model.addAttribute("pedidos", pedidosPendientes);
        return "admin/gestionar-pedidos"; 
    }

    @PostMapping("/confirmar")
    public String confirmarPedido(@RequestParam("pedidoId") Long pedidoId, RedirectAttributes attributes) {
        try {
            pedidoService.confirmarPedido(pedidoId);
            attributes.addFlashAttribute("successMessage", "Pedido #" + pedidoId + " confirmado con éxito. Se ha enviado la notificación.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al confirmar el pedido: " + e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/rechazar")
    public String rechazarPedido(@RequestParam("pedidoId") Long pedidoId, RedirectAttributes attributes) {
        try {
            pedidoService.rechazarPedido(pedidoId);
            attributes.addFlashAttribute("successMessage", "Pedido #" + pedidoId + " ha sido rechazado y el stock ha sido restaurado.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al rechazar el pedido: " + e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }
}