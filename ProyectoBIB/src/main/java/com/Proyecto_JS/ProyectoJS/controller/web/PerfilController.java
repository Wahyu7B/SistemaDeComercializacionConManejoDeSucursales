package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.hibernate.Hibernate; // AGREGAR
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // AGREGAR
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/perfil")
    @Transactional // AGREGAR ESTA LÍNEA
    public String mostrarPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró al usuario autenticado."));

        List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuario);

        // AGREGAR ESTE BLOQUE DE INICIALIZACIÓN
        pedidos.forEach(pedido -> {
            if (pedido.getDetalles() != null) {
                Hibernate.initialize(pedido.getDetalles());
                pedido.getDetalles().forEach(detalle -> {
                    if (detalle.getLibro() != null) {
                        Hibernate.initialize(detalle.getLibro());
                    }
                });
            }
            if (pedido.getSucursalRecojo() != null) {
                Hibernate.initialize(pedido.getSucursalRecojo());
            }
        });

        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);

        return "public/perfil";
    }
}
