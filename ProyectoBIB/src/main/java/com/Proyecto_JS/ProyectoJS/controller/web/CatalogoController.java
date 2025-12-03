package com.Proyecto_JS.ProyectoJS.controller.web;

import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.repository.CategoriaRepository;
import com.Proyecto_JS.ProyectoJS.repository.LibroRepository;
import com.Proyecto_JS.ProyectoJS.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @GetMapping
    public String catalogo(
            Model model,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "categoriaId", required = false) Long categoriaId,
            @RequestParam(name = "modo", required = false) String modo,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        // Determinar si es modo préstamo
        boolean modoPrestamo = "prestamo".equalsIgnoreCase(modo);
        
        Pageable pageable = PageRequest.of(page, 9);
        Page<Libro> paginaLibros;

        // Búsqueda según filtros
        if (query != null && !query.isBlank() && categoriaId != null) {
            paginaLibros = libroRepository.findByTituloContainingIgnoreCaseAndCategoriaId(query, categoriaId, pageable);
        } else if (query != null && !query.isBlank()) {
            paginaLibros = libroRepository.findByTituloContainingIgnoreCase(query, pageable);
        } else if (categoriaId != null) {
            paginaLibros = libroRepository.findByCategoriaId(categoriaId, pageable);
        } else {
            paginaLibros = libroRepository.findAll(pageable);
        }

        // Si es modo préstamo, filtrar solo libros con stock de préstamo
        if (modoPrestamo) {
            // Nota: Este filtro se puede optimizar creando un query method específico
            // Por ahora, el filtrado se hará en la vista con th:if
        }

        // Agregar atributos al modelo
        model.addAttribute("paginaLibros", paginaLibros);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("sucursales", sucursalRepository.findAll());
        model.addAttribute("query", query);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("modoPrestamo", modoPrestamo); // NUEVO: indicador de modo

        return "public/catalogo";
    }
}
