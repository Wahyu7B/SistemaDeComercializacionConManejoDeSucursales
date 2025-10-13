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
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 9);
        Page<Libro> paginaLibros;

        if (query != null && !query.isBlank() && categoriaId != null) {
            paginaLibros = libroRepository.findByTituloContainingIgnoreCaseAndCategoriaId(query, categoriaId, pageable);
        } else if (query != null && !query.isBlank()) {
            paginaLibros = libroRepository.findByTituloContainingIgnoreCase(query, pageable);
        } else if (categoriaId != null) {
            paginaLibros = libroRepository.findByCategoriaId(categoriaId, pageable);
        } else {
            paginaLibros = libroRepository.findAll(pageable);
        }

        model.addAttribute("paginaLibros", paginaLibros);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("sucursales", sucursalRepository.findAll());
        model.addAttribute("query", query);
        model.addAttribute("categoriaId", categoriaId);

        return "public/catalogo";
    }
}