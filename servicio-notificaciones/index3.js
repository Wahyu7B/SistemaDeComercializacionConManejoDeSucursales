const express = require('express');
const ExcelJS = require('exceljs');

const app = express();
app.use(express.json());

// Microservicio que exporta Excel en Node (simula exportación)
app.post('/api/exportar-excel', async (req, res) => {
  // Datos de muestra o usa req.body si quieres datos externos
  const libros = req.body.libros || [
    { isbn: '978-1-4028-9462-6', titulo: 'Libro Demo', autor: 'Autor X', precio: 50.5 },
    { isbn: '978-1-4028-9462-7', titulo: 'Segundo Libro', autor: 'Autor Y', precio: 41.2 },
  ];

  // Crea el archivo Excel con ExcelJS
  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet('Catálogo');

  // Encabezados
  sheet.columns = [
    { header: 'ISBN', key: 'isbn', width: 20 },
    { header: 'Título', key: 'titulo', width: 32 },
    { header: 'Autor', key: 'autor', width: 20 },
    { header: 'Precio', key: 'precio', width: 10 },
  ];

  // Agrega los datos
  libros.forEach(libro => sheet.addRow(libro));

  // Exporta como archivo XLSX binario
  res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
  res.setHeader('Content-Disposition', 'attachment; filename="Catalogo_Libros.xlsx"');

  await workbook.xlsx.write(res);
  res.end();
});

app.listen(3003, () => {
  console.log('Microservicio Excel corriendo en http://localhost:3003');
});