const express = require('express');
const fs = require('fs').promises;
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(express.json());
app.use(express.static('public'));

// Importar rutas
const sucursalesRoutes = require('./src/routes/sucursales');
const productosRoutes = require('./src/routes/productos');
const ventasRoutes = require('./src/routes/ventas');

// Usar rutas
app.use('/api/sucursales', sucursalesRoutes);
app.use('/api/productos', productosRoutes);
app.use('/api/ventas', ventasRoutes);

app.listen(PORT, () => {
    console.log(`Servidor corriendo en puerto ${PORT}`);
});