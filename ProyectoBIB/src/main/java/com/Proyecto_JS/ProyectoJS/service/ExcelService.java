package com.Proyecto_JS.ProyectoJS.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyecto_JS.ProyectoJS.entity.Libro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {
    
    @Autowired
    private LibroService libroService; 
    
    public byte[] generarCatalogoExcel() throws IOException {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Catálogo de Libros");
        
        //encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Título", "Autor", "Editorial", "Año", "ISBN", "Precio", "Stock"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            
            // Estilo
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            cell.setCellStyle(headerStyle);
        }
        
        // Llenar datos
        int rowNum = 1;
        for (Libro libro : libros) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(libro.getId());
            row.createCell(1).setCellValue(libro.getTitulo());
            row.createCell(2).setCellValue(libro.getAutor());
            row.createCell(3).setCellValue(libro.getEditorial());
            row.createCell(4).setCellValue(libro.getAnio());
            row.createCell(5).setCellValue(libro.getIsbn());
            row.createCell(6).setCellValue(libro.getPrecioVenta().doubleValue());
        }
        
        // Autoajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
}
