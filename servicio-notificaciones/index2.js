// Ubicación: servicio-reportes-pdf/index.js

const express = require('express');
const PDFDocument = require('pdfkit'); // Librería para generar el PDF
const fs = require('fs');
const app = express();
const PORT = 4001; // Puerto diferente para evitar conflictos
app.use(express.json()); // Para que pueda leer el JSON que le envía Spring Boot

// 1. Endpoint para generar y devolver el PDF
app.post('/api/generar-top-vendidos', (req, res) => {
    console.log("Petición recibida para generar Reporte PDF...");

    // Datos que Spring Boot nos envía (lista de libros)
    const topVendidos = req.body.data;
    
    // Configuración de la respuesta HTTP
    res.setHeader('Content-Type', 'application/pdf');
    res.setHeader('Content-Disposition', 'attachment; filename="Reporte_Top_Vendidos.pdf"');

    // 2. Creación del documento PDF
    const doc = new PDFDocument();
    
    // Pipe the document to the response (enviando el PDF como un stream)
    doc.pipe(res); 

    // Título y encabezado
    doc.fontSize(18).text('Reporte de Libros Más Vendidos', { align: 'center' });
    doc.fontSize(10).text(`Generado el: ${new Date().toLocaleDateString()}`, { align: 'center' });
    doc.moveDown();
    
    // 3. Generación del cuerpo del reporte (Tabla)
    doc.fontSize(12).text('Top 10 Libros (Basado en Unidades Vendidas):');
    doc.moveDown(0.5);

    // Encabezados de la tabla
    const yInitial = doc.y;
    doc.text('Título', 50, yInitial, { width: 250, continued: true });
    doc.text('Autor', 300, yInitial, { width: 100, continued: true });
    doc.text('Unidades', 420, yInitial, { width: 60, align: 'right', continued: true });
    doc.text('Ingreso (S/)', 500, yInitial, { width: 60, align: 'right' });
    doc.y += 15;
    doc.lineWidth(0.5).moveTo(50, doc.y).lineTo(560, doc.y).stroke(); // Línea separadora
    doc.y += 5;

    // Filas de la tabla
    topVendidos.forEach((libro) => {
        doc.fontSize(9);
        doc.text(libro.titulo, 50, doc.y, { width: 250, continued: true });
        doc.text(libro.autor, 300, doc.y, { width: 100, continued: true });
        doc.text(String(libro.unidades_vendidas), 420, doc.y, { width: 60, align: 'right', continued: true });
        doc.text(libro.ingreso_total.toFixed(2), 500, doc.y, { width: 60, align: 'right' });
        doc.moveDown(0.5); 
    });

    // 4. Finalizamos y enviamos el documento
    doc.end();
});

// 5. Inicia el servidor
app.listen(PORT, () => {
    console.log(`✅ Microservicio PDF corriendo en http://localhost:${PORT}`);
});