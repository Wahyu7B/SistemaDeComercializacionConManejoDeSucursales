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
        log.info("Iniciando generación de catálogo Excel");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Catálogo");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Información");
        headerRow.createCell(2).setCellValue("Total Libros");

        List<Libro> libros = libroRepository.findAll();
        
        String titulo = "Catálogo de Libros";
        validarTitulo(titulo);
        
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(1);
        dataRow.createCell(1).setCellValue("Libros disponibles en el sistema");
        dataRow.createCell(2).setCellValue(libros.size());

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
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
        headerRow.createCell(1).setCellValue("Información");
        headerRow.createCell(2).setCellValue("Total Registros");

        List<TopVendidoView> topVendidos = topVendidoRepository.findAll();
        
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(1);
        dataRow.createCell(1).setCellValue("Libros más vendidos");
        dataRow.createCell(2).setCellValue(topVendidos.size());

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        log.info("Top vendidos Excel generado exitosamente con {} registros", topVendidos.size());
        return outputStream.toByteArray();
    }
}
