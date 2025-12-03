package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.*;
import com.Proyecto_JS.ProyectoJS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/prestamos")
public class SolicitudPrestamoController {

    @Autowired
    private PrestamoRepository prestamoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private SucursalRepository sucursalRepository;

    // ❌ ELIMINADO - Ya existe en PrestamoController
    // @GetMapping("/mis-prestamos")
    // public String misPrestamos(Model model, Principal principal) { ... }

    @PostMapping("/solicitar")
    public String solicitarPrestamo(
            @RequestParam Long libroId,
            @RequestParam Long sucursalId,
            @RequestParam(required = false) String comentarios,
            Principal principal,
            RedirectAttributes attr) {
        
        if (principal == null) return "redirect:/login";
        
        try {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
            Libro libro = libroRepository.findById(libroId).orElseThrow();
            Sucursal sucursal = sucursalRepository.findById(sucursalId).orElseThrow();
            
            // Crear préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setUsuario(usuario);
            prestamo.setSucursal(sucursal);
            prestamo.setFechaSolicitud(LocalDate.now());
            prestamo.setFechaPrestamo(LocalDateTime.now());
            prestamo.setFechaDevolucionEsperada(LocalDateTime.now().plusDays(15));
            prestamo.setDiasPrestamo(15);
            prestamo.setEstado(Prestamo.EstadoPrestamo.ACTIVO);
            prestamo.setComentarios(comentarios);
            prestamo.setObservaciones("Solicitud pendiente de aprobación");
            
            // Crear detalle con el libro
            PrestamoDetalle detalle = new PrestamoDetalle();
            detalle.setLibro(libro);
            detalle.setCantidad(1);
            prestamo.addDetalle(detalle);
            
            prestamoRepository.save(prestamo);
            
            attr.addFlashAttribute("successMessage", "✅ Solicitud enviada correctamente");
            
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", "❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/prestamos/mis-prestamos";
    }
}
