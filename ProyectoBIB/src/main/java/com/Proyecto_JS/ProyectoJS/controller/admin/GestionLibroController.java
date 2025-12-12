package com.Proyecto_JS.ProyectoJS.controller.admin;

import com.Proyecto_JS.ProyectoJS.entity.Categoria;
import com.Proyecto_JS.ProyectoJS.entity.Inventario;
import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.service.CategoriaService;
import com.Proyecto_JS.ProyectoJS.service.InventarioService;
import com.Proyecto_JS.ProyectoJS.service.LibroService;

import org.hibernate.Hibernate; // AGREGAR
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // AGREGAR
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/admin/libros")
public class GestionLibroController {

    @Autowired private LibroService libroService;
    @Autowired private InventarioService inventarioService;
    @Autowired private CategoriaService categoriaService;

    // ==================== LISTAR CON PAGINACIÓN ====================
    
    @GetMapping("")
    @Transactional // AGREGAR @Transactional
    public String mostrarInventario(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        try {
            // Crear paginación
            Pageable pageable = PageRequest.of(page, size, Sort.by("libro.titulo").ascending());
            
            // Obtener inventario paginado
            Page<Inventario> inventarioPaginado = inventarioService.obtenerInventarioPaginado(pageable);
            
            // INICIALIZAR TODAS LAS RELACIONES LAZY
            inventarioPaginado.getContent().forEach(item -> {
                // Inicializar libro
                if (item.getLibro() != null) {
                    Hibernate.initialize(item.getLibro());
                    
                    // Inicializar categoría del libro
                    if (item.getLibro().getCategoria() != null) {
                        Hibernate.initialize(item.getLibro().getCategoria());
                    }
                }
                
                // Inicializar sucursal
                if (item.getSucursal() != null) {
                    Hibernate.initialize(item.getSucursal());
                }
            });
            
            // Agregar datos al modelo
            model.addAttribute("inventario", inventarioPaginado.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", inventarioPaginado.getTotalPages());
            model.addAttribute("totalItems", inventarioPaginado.getTotalElements());
            
            // Categorías y libro nuevo
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            model.addAttribute("libroNuevo", new Libro());
            
            System.out.println("📚 Inventario cargado - Página " + (page + 1) + "/" + inventarioPaginado.getTotalPages());
            System.out.println("📦 Mostrando " + inventarioPaginado.getContent().size() + " de " + inventarioPaginado.getTotalElements() + " libros");
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar inventario: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Error al cargar inventario: " + e.getMessage());
            model.addAttribute("inventario", new ArrayList<>());
            model.addAttribute("categorias", new ArrayList<>());
            model.addAttribute("libroNuevo", new Libro());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
        }
        
        return "admin/gestionar-libros";
    }
    
    // ==================== GUARDAR LIBRO ====================
    
    @PostMapping("/guardar")
    public String guardarLibro(@ModelAttribute("libroNuevo") Libro libro, RedirectAttributes attributes) {
        try {
            libro.setEstado(Libro.EstadoLibro.ACTIVO);
            libro.setCreatedAt(java.time.LocalDateTime.now());
            libro.setUpdatedAt(java.time.LocalDateTime.now());
            libroService.guardarLibro(libro);
            
            attributes.addFlashAttribute("successMessage", "✅ Libro agregado con éxito: " + libro.getTitulo());
            System.out.println("✅ Nuevo libro guardado: " + libro.getTitulo());
            
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "❌ Error al agregar el libro: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/libros";
    }

    // ==================== EDITAR LIBRO ====================
    
    @PostMapping("/editar")
    public String editarLibro(@ModelAttribute Libro libro, RedirectAttributes attr) {
        try {
            // Obtener el libro existente para preservar datos
            Libro libroExistente = libroService.obtenerLibroPorId(libro.getId());
            
            // Actualizar solo los campos modificables
            libroExistente.setTitulo(libro.getTitulo());
            libroExistente.setAutor(libro.getAutor());
            libroExistente.setIsbn(libro.getIsbn());
            libroExistente.setEditorial(libro.getEditorial());
            libroExistente.setAnio(libro.getAnio());
            libroExistente.setPrecioVenta(libro.getPrecioVenta());
            libroExistente.setCategoria(libro.getCategoria());
            libroExistente.setPortadaUrl(libro.getPortadaUrl());
            libroExistente.setDescripcion(libro.getDescripcion());
            libroExistente.setUpdatedAt(java.time.LocalDateTime.now());
            
            libroService.guardarLibro(libroExistente);
            
            attr.addFlashAttribute("successMessage", "✅ Libro actualizado: " + libro.getTitulo());
            System.out.println("✅ Libro editado ID " + libro.getId() + ": " + libro.getTitulo());
            
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", "❌ Error al editar: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/admin/libros";
    }

    // ==================== ACTUALIZAR STOCK ====================
    
    @PostMapping("/actualizar-stock")
    public String actualizarStock(
            @RequestParam Long inventarioId, 
            @RequestParam int nuevoStock, 
            RedirectAttributes attributes) {
        
        try {
            inventarioService.actualizarStock(inventarioId, nuevoStock);
            
            attributes.addFlashAttribute("successMessage", "✅ Stock actualizado correctamente");
            System.out.println("✅ Stock actualizado - Inventario ID: " + inventarioId + " → " + nuevoStock);
            
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "❌ Error al actualizar el stock: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/admin/libros";
    }

    // ==================== ELIMINAR LIBRO ====================
    
    @PostMapping("/eliminar")
    public String eliminarLibro(@RequestParam Long libroId, RedirectAttributes attr) {
        try {
            Libro libro = libroService.obtenerLibroPorId(libroId);
            String titulo = libro.getTitulo();
            
            libroService.eliminarLibro(libroId);
            
            attr.addFlashAttribute("successMessage", "✅ Libro eliminado: " + titulo);
            System.out.println("✅ Libro eliminado ID " + libroId + ": " + titulo);
            
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", "❌ Error al eliminar: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/admin/libros";
    }
}
