package com.Proyecto_JS.ProyectoJS.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Proyecto_JS.ProyectoJS.repository.LibroRepository;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.repository.PrestamoRepository;
import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.entity.Prestamo;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PrestamoRepository prestamoRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ==================== DASHBOARD PRINCIPAL ====================
    
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        try {
            // Datos básicos
            Long totalLibros = libroRepository.count();
            Long totalUsuarios = usuarioRepository.count();
            
            // Préstamos (solo contador para dashboard)
            long prestamosPendientes = prestamoRepository.findAll()
                .stream()
                .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO)
                .filter(p -> p.getObservaciones() == null || !p.getObservaciones().contains("Aprobado"))
                .count();
            
            model.addAttribute("prestamosPendientes", prestamosPendientes);
            
            // Cálculos
            List<Libro> librosReales = libroRepository.findAll();
            int ventasHoy = Math.max(1, Math.toIntExact(totalLibros / 15));
            
            BigDecimal precioPromedio = BigDecimal.valueOf(35.0);
            if (!librosReales.isEmpty()) {
                try {
                    BigDecimal suma = BigDecimal.ZERO;
                    int contador = 0;
                    for (Libro libro : librosReales) {
                        if (libro.getPrecioVenta() != null) {
                            suma = suma.add(libro.getPrecioVenta());
                            contador++;
                        }
                    }
                    if (contador > 0) {
                        precioPromedio = suma.divide(BigDecimal.valueOf(contador), BigDecimal.ROUND_HALF_UP);
                    }
                } catch (Exception e) {
                    precioPromedio = BigDecimal.valueOf(35.0);
                }
            }
            
            BigDecimal ingresosDia = precioPromedio.multiply(BigDecimal.valueOf(ventasHoy));
            int pedidosPendientes = Math.max(0, Math.toIntExact(totalUsuarios / 4));
            
            // Datos de BD reales
            int totalSucursales = 2;
            try {
                Integer sucursales = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sucursales WHERE estado = 'ACTIVA'", 
                    Integer.class
                );
                if (sucursales != null && sucursales > 0) {
                    totalSucursales = sucursales;
                }
            } catch (Exception e) {
                totalSucursales = 2;
            }
            
            try {
                Integer pedidosReales = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM pedidos WHERE estado IN ('CREADO', 'PAGO_EN_REVISION')", 
                    Integer.class
                );
                if (pedidosReales != null) {
                    pedidosPendientes = pedidosReales;
                }
            } catch (Exception e) {
                // Mantener valor calculado
            }
            
            try {
                BigDecimal ingresosReales = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(total), 0) FROM pedidos WHERE estado = 'PAGADO' AND DATE(created_at) = CURDATE()", 
                    BigDecimal.class
                );
                if (ingresosReales != null && ingresosReales.compareTo(BigDecimal.ZERO) > 0) {
                    ingresosDia = ingresosReales;
                }
            } catch (Exception e) {
                // Mantener valor calculado
            }
            
            // Atributos para la vista
            model.addAttribute("totalLibros", totalLibros);
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("ventasHoy", ventasHoy);
            model.addAttribute("ingresosDia", ingresosDia.setScale(1, BigDecimal.ROUND_HALF_UP));
            model.addAttribute("totalSucursales", totalSucursales);
            model.addAttribute("pedidosPendientes", pedidosPendientes);
            
            // Tip del día
            String tipDelDia;
            if (prestamosPendientes > 0) {
                tipDelDia = "Tienes " + prestamosPendientes + " solicitudes de préstamo pendientes de revisión.";
            } else if (totalLibros > 50) {
                tipDelDia = "¡Excelente catálogo! Tienes " + totalLibros + " libros disponibles.";
            } else if (pedidosPendientes > 5) {
                tipDelDia = "Tienes " + pedidosPendientes + " pedidos pendientes de atención.";
            } else {
                tipDelDia = "Sistema operando correctamente. " + totalLibros + " libros • " + totalUsuarios + " usuarios activos.";
            }
            
            model.addAttribute("tipDelDia", tipDelDia);
            
            System.out.println("📊 Dashboard cargado - Préstamos pendientes: " + prestamosPendientes);
            
        } catch (Exception e) {
            System.err.println("❌ Error en dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Valores por defecto
            model.addAttribute("totalLibros", 0L);
            model.addAttribute("totalUsuarios", 0L);
            model.addAttribute("ventasHoy", 0);
            model.addAttribute("ingresosDia", BigDecimal.ZERO);
            model.addAttribute("totalSucursales", 0);
            model.addAttribute("pedidosPendientes", 0);
            model.addAttribute("prestamosPendientes", 0L);
            model.addAttribute("tipDelDia", "Sistema iniciando...");
        }
        
        return "admin/dashboard";
    }

    // ==================== PÁGINA DE GESTIÓN DE PRÉSTAMOS ====================
    
    @GetMapping("/prestamos")
    public String gestionarPrestamos(Model model) {
        try {
            // Obtener todas las solicitudes activas
            List<Prestamo> todasSolicitudes = prestamoRepository.findAll()
                .stream()
                .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO)
                .collect(Collectors.toList());
            
            // Separar por estado
            List<Prestamo> pendientes = todasSolicitudes.stream()
                .filter(p -> p.getObservaciones() == null || !p.getObservaciones().contains("Aprobado"))
                .collect(Collectors.toList());
            
            List<Prestamo> aprobados = todasSolicitudes.stream()
                .filter(p -> p.getObservaciones() != null && p.getObservaciones().contains("Aprobado"))
                .collect(Collectors.toList());
            
            model.addAttribute("solicitudesPrestamo", todasSolicitudes);
            model.addAttribute("prestamosPendientes", (long) pendientes.size());
            model.addAttribute("prestamosAprobados", (long) aprobados.size());
            model.addAttribute("totalPrestamos", (long) todasSolicitudes.size());
            
            System.out.println("📚 Préstamos: " + todasSolicitudes.size() + " | Pendientes: " + pendientes.size() + " | Aprobados: " + aprobados.size());
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar préstamos: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("solicitudesPrestamo", new ArrayList<>());
            model.addAttribute("prestamosPendientes", 0L);
            model.addAttribute("prestamosAprobados", 0L);
            model.addAttribute("totalPrestamos", 0L);
        }
        
        return "admin/gestionar-prestamos";
    }

    // ==================== ACCIONES DE PRÉSTAMOS ====================
    
    @PostMapping("/prestamos/aprobar")
    public String aprobarPrestamo(@RequestParam Long prestamoId, 
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes attr) {
        try {
            Prestamo prestamo = prestamoRepository.findById(prestamoId)
                    .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            
            prestamo.setObservaciones("✅ Aprobado - Listo para retiro en " + prestamo.getSucursal().getNombre());
            prestamoRepository.save(prestamo);
            
            attr.addFlashAttribute("successMessage", "✅ Préstamo #" + prestamoId + " aprobado exitosamente");
            System.out.println("✅ Préstamo #" + prestamoId + " aprobado por " + userDetails.getUsername());
            
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", "❌ Error al aprobar: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/admin/prestamos";
    }

    @PostMapping("/prestamos/rechazar")
    public String rechazarPrestamo(@RequestParam Long prestamoId, 
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes attr) {
        try {
            Prestamo prestamo = prestamoRepository.findById(prestamoId)
                    .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
            
            prestamo.setEstado(Prestamo.EstadoPrestamo.DEVUELTO);
            prestamo.setObservaciones("❌ Rechazado por administrador");
            prestamoRepository.save(prestamo);
            
            attr.addFlashAttribute("successMessage", "❌ Préstamo #" + prestamoId + " rechazado");
            System.out.println("❌ Préstamo #" + prestamoId + " rechazado por " + userDetails.getUsername());
            
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", "❌ Error al rechazar: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/admin/prestamos";
    }
}
