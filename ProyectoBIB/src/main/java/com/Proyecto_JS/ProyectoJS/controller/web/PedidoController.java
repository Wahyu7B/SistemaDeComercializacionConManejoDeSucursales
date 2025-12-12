package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Carrito;
import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.SucursalRepository;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.service.CarritoService;
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.hibernate.Hibernate; // AGREGAR ESTE IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // AGREGAR ESTE IMPORT
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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

    // MÉTODO MODIFICADO - AGREGAR @Transactional E INICIALIZACIÓN
    @GetMapping("/checkout")
    @Transactional(readOnly = true) // AGREGAR ESTA ANOTACIÓN
    public String mostrarCheckout(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Carrito carrito = carritoService.obtenerCarritoDelUsuario(usuario.getId());
        
        // AGREGAR ESTAS LÍNEAS - INICIALIZAR COLECCIONES LAZY
        if (carrito != null && carrito.getItems() != null) {
            Hibernate.initialize(carrito.getItems());
            carrito.getItems().forEach(item -> {
                Hibernate.initialize(item.getLibro());
                if (item.getSucursal() != null) {
                    Hibernate.initialize(item.getSucursal());
                }
            });
        }
        
        model.addAttribute("carrito", carrito);
        model.addAttribute("sucursales", sucursalRepository.findAll());

        Map<String, Double> costosEnvio = new HashMap<>();
        costosEnvio.put("La Victoria", 12.00);
        costosEnvio.put("San Isidro", 15.00);
        costosEnvio.put("Miraflores", 15.00);
        costosEnvio.put("Surco", 18.00);
        costosEnvio.put("Callao", 20.00);
        costosEnvio.put("San Juan de Lurigancho", 25.00);
        costosEnvio.put("Ate", 22.00);
        costosEnvio.put("Breña", 13.00);
        costosEnvio.put("Lince", 13.00);
        costosEnvio.put("Pueblo Libre", 14.00);
        model.addAttribute("costosEnvio", costosEnvio);

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
            System.out.println(">>> procesarPedido()");
            System.out.println("    tipoEntrega = " + tipoEntrega);
            System.out.println("    sucursalRecojoId = " + sucursalRecojoId);
            System.out.println("    distrito = " + distrito);
            System.out.println("    direccion = " + direccion);

            if ("RECOJO_TIENDA".equals(tipoEntrega) && sucursalRecojoId == null) {
                System.out.println(">>> VALIDACION: falta sucursal para recojo en tienda");
                redirectAttributes.addFlashAttribute("errorMessage", "Debes seleccionar una sucursal.");
                return "redirect:/pedido/checkout";
            }

            if ("DELIVERY".equals(tipoEntrega)) {
                if (distrito == null || distrito.isEmpty()) {
                    System.out.println(">>> VALIDACION: falta distrito para delivery");
                    redirectAttributes.addFlashAttribute("errorMessage", "Debes seleccionar un distrito.");
                    return "redirect:/pedido/checkout";
                }
                if (direccion == null || direccion.trim().isEmpty()) {
                    System.out.println(">>> VALIDACION: falta dirección para delivery");
                    redirectAttributes.addFlashAttribute("errorMessage", "Debes ingresar una dirección.");
                    return "redirect:/pedido/checkout";
                }
            }

            Pedido nuevoPedido = pedidoService.crearPedido(
                    carritoId,
                    tipoEntrega,
                    sucursalRecojoId,
                    distrito,
                    direccion
            );

            System.out.println(">>> Pedido creado correctamente. ID = " + nuevoPedido.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "¡Pedido #" + nuevoPedido.getId() + " creado en estado pendiente de pago!"
            );

            // Redirige a la pantalla del QR (funciona para recojo y delivery)
            return "redirect:/pedido/confirmacion/" + nuevoPedido.getId();

        } catch (Exception e) {
            System.out.println(">>> EXCEPCION en procesarPedido:");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error al procesar el pedido: " + e.getMessage()
            );
            return "redirect:/pedido/checkout";
        }
    }

    // TAMBIÉN AGREGAR @Transactional AQUÍ
    @GetMapping("/confirmacion/{id}")
    @Transactional(readOnly = true)
    public String mostrarConfirmacionDePago(@PathVariable("id") Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        // INICIALIZAR TODAS LAS RELACIONES LAZY
        if (pedido.getDetalles() != null) {
            Hibernate.initialize(pedido.getDetalles());
            pedido.getDetalles().forEach(detalle -> {
                Hibernate.initialize(detalle.getLibro());
            });
        }
        
        // AGREGAR ESTA LÍNEA - Inicializar sucursalRecojo
        if (pedido.getSucursalRecojo() != null) {
            Hibernate.initialize(pedido.getSucursalRecojo());
        }

        model.addAttribute("pedido", pedido);
        return "public/confirmacion-pago";
    }



}
