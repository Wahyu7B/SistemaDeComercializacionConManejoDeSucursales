package com.Proyecto_JS.ProyectoJS.controller.admin;

import com.Proyecto_JS.ProyectoJS.entity.TopVendidoView;
import com.Proyecto_JS.ProyectoJS.repository.TopVendidoRepository;
import com.Proyecto_JS.ProyectoJS.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private TopVendidoRepository topVendidoRepository;
    
    @Autowired
    private ReporteService reporteService; 

    @GetMapping("")
    public String mostrarReportes(Model model) {
        List<TopVendidoView> topVendidos = topVendidoRepository.findAll();
        model.addAttribute("topVendidos", topVendidos);
        return "admin/reportes";
    }
    
    @GetMapping("/descargar-top-vendidos")
    public ResponseEntity<byte[]> descargarTopVendidosPDF() {
        List<TopVendidoView> topVendidos = topVendidoRepository.findAll();
        
        byte[] pdfBytes = reporteService.generarTopVendidosPDF(topVendidos);
        
        if (pdfBytes == null || pdfBytes.length == 0) {
            return ResponseEntity.status(500).body("Error al generar PDF".getBytes());
        }
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Reporte_Top_Vendidos.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }
}