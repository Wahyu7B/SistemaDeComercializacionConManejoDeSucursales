package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.dto.DevolucionDTO;
import com.Proyecto_JS.ProyectoJS.dto.PrestamoDTO;
import com.Proyecto_JS.ProyectoJS.entity.Prestamo;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.service.PrestamoService;
import com.Proyecto_JS.ProyectoJS.service.SucursalService;
import com.Proyecto_JS.ProyectoJS.service.LibroService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Listar préstamos del usuario autenticado (CLIENTE)
     */
    @GetMapping("/mis-prestamos")
    @Transactional
    public String misPrestamos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Obtener usuario autenticado
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener todos los préstamos del usuario
        List<Prestamo> todosPrestamos = prestamoService.listarPorUsuario(usuario.getId());
        
        System.out.println("📚 Total préstamos encontrados: " + todosPrestamos.size());

        // Inicializar relaciones lazy
        todosPrestamos.forEach(prestamo -> {
            // Inicializar sucursal
            if (prestamo.getSucursal() != null) {
                Hibernate.initialize(prestamo.getSucursal());
            }
            // Inicializar detalles y sus libros
            if (prestamo.getDetalles() != null) {
                Hibernate.initialize(prestamo.getDetalles());
                prestamo.getDetalles().forEach(detalle -> {
                    if (detalle.getLibro() != null) {
                        Hibernate.initialize(detalle.getLibro());
                    }
                });
            }
        });

        // Separar en activos e historial
        List<Prestamo> prestamosActivos = todosPrestamos.stream()
            .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO || 
                        p.getEstado() == Prestamo.EstadoPrestamo.VENCIDO)
            .collect(Collectors.toList());

        List<Prestamo> prestamosHistorial = todosPrestamos.stream()
            .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.DEVUELTO || 
                        p.getEstado() == Prestamo.EstadoPrestamo.RENOVADO)
            .collect(Collectors.toList());

        model.addAttribute("prestamosActivos", prestamosActivos);
        model.addAttribute("prestamosHistorial", prestamosHistorial);
        model.addAttribute("prestamos", todosPrestamos);
        model.addAttribute("usuario", usuario);
        
        return "public/mis-prestamos";
    }

    /**
     * Ver detalle de un préstamo
     */
    @GetMapping("/{id}")
@Transactional
public String verDetalle(@PathVariable Long id, Model model) {
    Prestamo prestamo = prestamoService.obtenerPorId(id);
    
    // Inicializar USUARIO
    if (prestamo.getUsuario() != null) {
        Hibernate.initialize(prestamo.getUsuario());
    }
    
    // Inicializar sucursal
    if (prestamo.getSucursal() != null) {
        Hibernate.initialize(prestamo.getSucursal());
    }
    
    // Inicializar detalles y sus libros
    if (prestamo.getDetalles() != null) {
        Hibernate.initialize(prestamo.getDetalles());
        prestamo.getDetalles().forEach(detalle -> {
            if (detalle.getLibro() != null) {
                Hibernate.initialize(detalle.getLibro());
            }
        });
    }
    
    model.addAttribute("prestamo", prestamo);
    model.addAttribute("detalles", prestamoService.obtenerDetalles(id));
    return "prestamos/detalle";
}

    /**
     * Formulario para crear nuevo préstamo (ADMIN)
     */
    @GetMapping("/admin/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("prestamoDTO", new PrestamoDTO());
        model.addAttribute("sucursales", sucursalService.listarActivas());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("libros", libroService.obtenerTodosLosLibros());
        return "prestamos/admin/nuevo";
    }

    /**
     * Procesar creación de préstamo (ADMIN)
     */
    @PostMapping("/admin/crear")
    public String crear(@Valid @ModelAttribute PrestamoDTO prestamoDTO,
                       BindingResult result,
                       @AuthenticationPrincipal UserDetails userDetails,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("sucursales", sucursalService.listarActivas());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("libros", libroService.obtenerTodosLosLibros());
            return "prestamos/admin/nuevo";
        }

        try {
            Usuario admin = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
            
            Prestamo prestamo = prestamoService.crearPrestamo(prestamoDTO, admin.getId());
            redirectAttributes.addFlashAttribute("success", "Préstamo registrado exitosamente");
            return "redirect:/prestamos/" + prestamo.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/prestamos/admin/nuevo";
        }
    }

    /**
     * Listar todos los préstamos (ADMIN)
     */
    @GetMapping("/admin/listar")
    @Transactional
    public String listarTodos(@RequestParam(required = false) String estado,
                             @RequestParam(required = false) String usuario,
                             Model model) {
        
        List<Prestamo> prestamos;
        
        if (estado != null && !estado.isEmpty()) {
            prestamos = prestamoService.listarPorEstado(Prestamo.EstadoPrestamo.valueOf(estado));
        } else {
            prestamos = prestamoService.listarTodos();
        }

        // Inicializar relaciones
        prestamos.forEach(prestamo -> {
            if (prestamo.getSucursal() != null) {
                Hibernate.initialize(prestamo.getSucursal());
            }
            if (prestamo.getDetalles() != null) {
                Hibernate.initialize(prestamo.getDetalles());
                prestamo.getDetalles().forEach(detalle -> {
                    if (detalle.getLibro() != null) {
                        Hibernate.initialize(detalle.getLibro());
                    }
                });
            }
        });

        List<Prestamo> prestamosActivos = prestamoService.listarPorEstado(Prestamo.EstadoPrestamo.ACTIVO);
        List<Prestamo> prestamosVencidos = prestamoService.listarPorEstado(Prestamo.EstadoPrestamo.VENCIDO);

        model.addAttribute("prestamos", prestamos);
        model.addAttribute("prestamosActivos", prestamosActivos);
        model.addAttribute("prestamosVencidos", prestamosVencidos);
        
        return "prestamos/admin/listar";
    }

    /**
     * Listar préstamos activos (ADMIN)
     */
    @GetMapping("/admin/activos")
    @Transactional
    public String listarActivos(Model model) {
        List<Prestamo> prestamosActivos = prestamoService.listarPorEstado(Prestamo.EstadoPrestamo.ACTIVO);
        
        // Inicializar relaciones
        prestamosActivos.forEach(prestamo -> {
            if (prestamo.getSucursal() != null) {
                Hibernate.initialize(prestamo.getSucursal());
            }
            if (prestamo.getDetalles() != null) {
                Hibernate.initialize(prestamo.getDetalles());
                prestamo.getDetalles().forEach(detalle -> {
                    if (detalle.getLibro() != null) {
                        Hibernate.initialize(detalle.getLibro());
                    }
                });
            }
        });
        
        model.addAttribute("prestamos", prestamosActivos);
        return "prestamos/admin/activos";
    }

    /**
     * Formulario de devolución (ADMIN)
     */
    /**
 * Formulario de devolución (ADMIN)
 */
@GetMapping("/admin/{id}/devolucion")
@Transactional
public String formularioDevolucion(@PathVariable Long id, Model model) {
    Prestamo prestamo = prestamoService.obtenerPorId(id);
    
    if (prestamo.getEstado() != Prestamo.EstadoPrestamo.ACTIVO && 
        prestamo.getEstado() != Prestamo.EstadoPrestamo.VENCIDO) {
        throw new RuntimeException("El préstamo no está en estado activo o vencido");
    }
    
    // Inicializar USUARIO
    if (prestamo.getUsuario() != null) {
        Hibernate.initialize(prestamo.getUsuario());
    }
    
    // Inicializar sucursal
    if (prestamo.getSucursal() != null) {
        Hibernate.initialize(prestamo.getSucursal());
    }
    
    // Inicializar detalles y sus libros
    if (prestamo.getDetalles() != null) {
        Hibernate.initialize(prestamo.getDetalles());
        prestamo.getDetalles().forEach(detalle -> {
            if (detalle.getLibro() != null) {
                Hibernate.initialize(detalle.getLibro());
            }
        });
    }
    
    model.addAttribute("prestamo", prestamo);
    model.addAttribute("detalles", prestamoService.obtenerDetalles(id));
    
    DevolucionDTO devolucionDTO = new DevolucionDTO();
    devolucionDTO.setPrestamoId(id);
    model.addAttribute("devolucionDTO", devolucionDTO);
    
    return "prestamos/admin/devolucion";
}


    /**
     * Procesar devolución (ADMIN)
     */
    @PostMapping("/admin/devolucion")
    public String procesarDevolucion(@Valid @ModelAttribute DevolucionDTO devolucionDTO,
                                    BindingResult result,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        
        if (result.hasErrors()) {
            Prestamo prestamo = prestamoService.obtenerPorId(devolucionDTO.getPrestamoId());
            model.addAttribute("prestamo", prestamo);
            model.addAttribute("detalles", prestamoService.obtenerDetalles(devolucionDTO.getPrestamoId()));
            return "prestamos/admin/devolucion";
        }

        try {
            Usuario admin = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
            
            devolucionDTO.setAdminDevolucionId(admin.getId());
            
            Prestamo prestamo = prestamoService.procesarDevolucion(devolucionDTO);
            redirectAttributes.addFlashAttribute("success", "Devolución procesada exitosamente");
            return "redirect:/prestamos/" + prestamo.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/prestamos/admin/" + devolucionDTO.getPrestamoId() + "/devolucion";
        }
    }
}
