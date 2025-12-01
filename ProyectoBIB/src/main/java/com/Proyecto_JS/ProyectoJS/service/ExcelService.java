package com.Proyecto_JS.ProyectoJS.service;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto_JS.ProyectoJS.repository.LibroRepository;
import com.Proyecto_JS.ProyectoJS.repository.TopVendidoRepository;
import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.entity.TopVendidoView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private TopVendidoRepository topVendidoRepository;
    
    private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

    private void validarTitulo(String titulo) {
        checkArgument(StringUtils.isNotBlank(titulo), "El título no puede estar vacío");
    }

    public byte[] generarCatalogoExcel() throws IOException {
        log.info("Iniciando generación de catálogo Excel detallado");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Catálogo de Libros");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("ISBN");
        headerRow.createCell(2).setCellValue("Título");
        headerRow.createCell(3).setCellValue("Autor");
        headerRow.createCell(4).setCellValue("Editorial");
        headerRow.createCell(5).setCellValue("Año");
        headerRow.createCell(6).setCellValue("Precio Venta (S/.)");
        headerRow.createCell(7).setCellValue("Estado");
        headerRow.createCell(8).setCellValue("Categoría");

        List<Libro> libros = libroRepository.findAll();
        
        String titulo = "Catálogo de Libros";
        validarTitulo(titulo);
        
        int fila = 1;
        for (Libro libro : libros) {
            Row dataRow = sheet.createRow(fila++);
            
            dataRow.createCell(0).setCellValue(libro.getId() != null ? libro.getId() : 0);
            
            dataRow.createCell(1).setCellValue(libro.getIsbn() != null ? libro.getIsbn() : "Sin ISBN");
            
            String tituloLibro = libro.getTitulo() != null ? libro.getTitulo() : "Sin título";
            dataRow.createCell(2).setCellValue(tituloLibro);
            
            dataRow.createCell(3).setCellValue(libro.getAutor() != null ? libro.getAutor() : "Sin autor");
            
            dataRow.createCell(4).setCellValue(libro.getEditorial() != null ? libro.getEditorial() : "Sin editorial");
            
            dataRow.createCell(5).setCellValue(libro.getAnio() != null ? libro.getAnio() : 0);
            
            if (libro.getPrecioVenta() != null) {
                dataRow.createCell(6).setCellValue(libro.getPrecioVenta().doubleValue());
            } else {
                dataRow.createCell(6).setCellValue(0.0);
            }
            
            dataRow.createCell(7).setCellValue(libro.getEstado() != null ? libro.getEstado().toString() : "Sin estado");
            
            try {
                if (libro.getCategoria() != null) {
                    dataRow.createCell(8).setCellValue(libro.getCategoria().getNombre());
                } else {
                    dataRow.createCell(8).setCellValue("Sin categoría");
                }
            } catch (Exception e) {
                dataRow.createCell(8).setCellValue("Sin categoría");
            }
        }

        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        for (int i = 0; i < 9; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        log.info("Catálogo Excel generado exitosamente con {} libros", libros.size());
        return outputStream.toByteArray();
    }

    public byte[] generarTopVendidosExcel() throws IOException {
        log.info("Iniciando generación de top vendidos Excel");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Top Vendidos");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Ranking");
        headerRow.createCell(1).setCellValue("Información del Item");
        headerRow.createCell(2).setCellValue("Estado");

        List<TopVendidoView> topVendidos = topVendidoRepository.findAll();
        
        int fila = 1;
        int ranking = 1;
        for (TopVendidoView item : topVendidos) {
            Row dataRow = sheet.createRow(fila++);
            
            dataRow.createCell(0).setCellValue(ranking++);
            
            String info = "Top Vendido #" + ranking + " - " + (item != null ? "Registrado" : "Sin datos");
            dataRow.createCell(1).setCellValue(info);
            
            dataRow.createCell(2).setCellValue("Activo en ranking");
        }

        if (topVendidos.isEmpty()) {
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(1);
            dataRow.createCell(1).setCellValue("No hay registros de ventas disponibles");
            dataRow.createCell(2).setCellValue("Sin datos");
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        for (int i = 0; i < 3; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        log.info("Top vendidos Excel generado con {} registros", topVendidos.size());
        return outputStream.toByteArray();
    }
}
