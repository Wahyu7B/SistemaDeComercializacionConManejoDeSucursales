const express = require('express');
const bodyParser = require('body-parser');
const PDFDocument = require('pdfkit');

const app = express();
const port = 4001; // ✅ El puerto correcto

app.use(bodyParser.json());

// ✅ La ruta correcta
app.post('/reporte-libros-vendidos', (req, res) => {
    try {
        console.log('Petición recibida para generar Reporte PDF...');
        const libros = req.body.libros;
        const fecha = req.body.fecha;

        const doc = new PDFDocument({ margin: 50 });
        const buffers = [];
        doc.on('data', buffers.push.bind(buffers));
        doc.on('end', () => {
            const pdfData = Buffer.concat(buffers);
            res.writeHead(200, {
                'Content-Length': Buffer.byteLength(pdfData),
                'Content-Type': 'application/pdf',
                'Content-Disposition': 'attachment;filename=reporte.pdf',
            }).end(pdfData);
        });

        // Header del documento
        doc.fontSize(20).text('Reporte de Libros Más Vendidos', { align: 'center' });
        doc.fontSize(12).text(`Generado el: ${fecha}`, { align: 'center' });
        doc.moveDown(2);

        // Definir la tabla
        const tableTop = 150;
        const tableHeaders = ['ID del Libro', 'Título', 'Unidades Vendidas'];
        const columnWidths = [100, 300, 150];
        let currentX = 50;

        // Dibujar encabezados de la tabla
        doc.fontSize(12).font('Helvetica-Bold');
        tableHeaders.forEach((header, i) => {
            doc.text(header, currentX, tableTop, { width: columnWidths[i], align: 'left' });
            currentX += columnWidths[i];
        });
        doc.font('Helvetica');
        
        // Línea debajo de los encabezados
        doc.moveTo(50, tableTop + 20)
           .lineTo(550, tableTop + 20)
           .stroke();
           
        // Contenido de la tabla
        let currentY = tableTop + 30;
        libros.forEach(libro => {
            const rowData = [
                libro.id,
                libro.nombre,
                libro.totalVendido
            ];
            
            currentX = 50;
            rowData.forEach((cell, i) => {
                doc.text(String(cell), currentX, currentY, { width: columnWidths[i], align: 'left' });
                currentX += columnWidths[i];
            });

            currentY += 25; // Espacio para la siguiente fila
        });

        console.log('PDF generado exitosamente.');
        doc.end();

    } catch (error) {
        console.error('Error al generar PDF:', error);
        res.status(500).send('Error interno al generar el PDF.');
    }
});

app.listen(port, () => {
    console.log(`Servicio de reportes PDF escuchando en http://localhost:${port}`);
});