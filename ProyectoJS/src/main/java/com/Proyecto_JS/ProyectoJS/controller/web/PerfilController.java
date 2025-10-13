// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/controller/web/PerfilController.java
package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Pedido;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
    public String mostrarPerfil(Model model) {
        // 1. Obtiene el email del usuario que ha iniciado sesión
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2. Busca al usuario completo en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró al usuario autenticado."));

        // 3. Busca el historial de pedidos de ese usuario
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuario);

        // 4. Pasa los datos a la vista
        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);

        return "public/perfil"; // Apunta a la nueva vista que vamos a crear
    }
}