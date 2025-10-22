package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Carrito;
import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.SucursalRepository;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.service.CarritoService;
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private CarritoService carritoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @GetMapping("/checkout")
    public String mostrarCheckout(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Carrito carrito = carritoService.obtenerCarritoDelUsuario(usuario.getId());
        model.addAttribute("carrito", carrito);
        
        model.addAttribute("sucursales", sucursalRepository.findAll());
        
        return "public/checkout";
    }

    @PostMapping("/procesar")
    public String procesarPedido(@RequestParam Long carritoId,
                                 @RequestParam String tipoEntrega,
                                 @RequestParam(required = false) Long sucursalRecojoId,
                                 @RequestParam(required = false) String distrito,
                                 @RequestParam(required = false) String direccion,
                                 RedirectAttributes redirectAttributes) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(carritoId, null, tipoEntrega, sucursalRecojoId); // Pasamos null para direccionEnvioId ya que no se usa
            redirectAttributes.addFlashAttribute("successMessage", "¡Pedido #" + nuevoPedido.getId() + " creado con éxito!");
            return "redirect:/pedido/confirmacion/" + nuevoPedido.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar el pedido: " + e.getMessage());
            return "redirect:/pedido/checkout";
        }
    }
    
    @GetMapping("/confirmacion/{id}")
    public String mostrarConfirmacionDePago(@PathVariable("id") Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
        
        model.addAttribute("pedido", pedido);
        return "public/confirmacion-pago"; 
    }
}