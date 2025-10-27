package com.Proyecto_JS.ProyectoJS.controller.admin;

import com.Proyecto_JS.ProyectoJS.entity.TopVendidoView;
import com.Proyecto_JS.ProyectoJS.repository.TopVendidoRepository;
import com.Proyecto_JS.ProyectoJS.service.ReporteService;
import com.Proyecto_JS.ProyectoJS.service.ExcelService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private TopVendidoRepository topVendidoRepository;
    
    @Autowired
    private ReporteService reporteService;
    
    @Autowired
    private ExcelService excelService;

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
    
    // Para exportar en excel 
    @GetMapping("/exportar-catalogo-excel")
    public ResponseEntity<byte[]> exportarCatalogoExcel() {
        try {
            byte[] excelBytes = excelService.generarCatalogoExcel();
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Catalogo_Libros.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
                
        } catch (IOException e) {
            // logger por si un error
            System.err.println("Error al generar Excel: " + e.getMessage());
            return ResponseEntity.status(500)
                .body("Error al generar el archivo Excel".getBytes());
        }
    }

    
}
