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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {
    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

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
    
    // Para exportar catálogo completo en excel 
    @GetMapping("/exportar-catalogo-excel")
    public ResponseEntity<byte[]> exportarCatalogoExcel() {
        log.info("Solicitud de exportación de catálogo Excel");
        try {
            byte[] excelBytes = excelService.generarCatalogoExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Catalogo_Libros.xlsx\"");
            headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);

        } catch (IOException e) {
            log.error("Error al generar Excel: {}", e.getMessage());
            return ResponseEntity.status(500)
                .body("Error al generar el archivo Excel".getBytes());
        }
    }

    // Para exportar top vendidos en excel
    @GetMapping("/exportar-top-vendidos-excel")
    public ResponseEntity<byte[]> exportarTopVendidosExcel() {
        log.info("Solicitud de exportación de top vendidos Excel");
        try {
            byte[] excelBytes = excelService.generarTopVendidosExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Top_Vendidos.xlsx\"");
            headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);

        } catch (IOException e) {
            log.error("Error al generar Excel de top vendidos: {}", e.getMessage());
            return ResponseEntity.status(500)
                .body("Error al generar el archivo Excel".getBytes());
        }
    }
}
