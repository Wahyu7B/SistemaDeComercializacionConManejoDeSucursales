const express = require('express');
const SucursalesController = require('../controllers/sucursalesController');

const router = express.Router();
const controller = new SucursalesController();

// GET /api/sucursales
router.get('/', async (req, res) => {
    try {
        const sucursales = await controller.obtenerSucursales();
        res.json(sucursales);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/sucursales/:id
router.get('/:id', async (req, res) => {
    try {
        const sucursal = await controller.obtenerSucursalPorId(req.params.id);
        if (!sucursal) {
            return res.status(404).json({ error: 'Sucursal no encontrada' });
        }
        res.json(sucursal);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/sucursales
router.post('/', async (req, res) => {
    try {
        const nuevaSucursal = await controller.crearSucursal(req.body);
        res.status(201).json(nuevaSucursal);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;