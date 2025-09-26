const FileManager = require('../utils/fileManager');

class SucursalesController {
    constructor() {
        this.fileManager = new FileManager('sucursales.json');
    }

    async obtenerSucursales() {
        const data = await this.fileManager.readData();
        return data.sucursales;
    }

    async obtenerSucursalPorId(id) {
        const data = await this.fileManager.readData();
        return data.sucursales.find(suc => suc.id === id);
    }

    async crearSucursal(sucursalData) {
        const data = await this.fileManager.readData();
        const nuevaSucursal = {
            id: 'suc' + Date.now(),
            ...sucursalData,
            fechaCreacion: new Date().toISOString().split('T')[0]
        };
        
        data.sucursales.push(nuevaSucursal);
        await this.fileManager.writeData(data);
        return nuevaSucursal;
    }

    async actualizarSucursal(id, sucursalData) {
        const data = await this.fileManager.readData();
        const index = data.sucursales.findIndex(suc => suc.id === id);
        
        if (index === -1) return null;
        
        data.sucursales[index] = { ...data.sucursales[index], ...sucursalData };
        await this.fileManager.writeData(data);
        return data.sucursales[index];
    }
}

module.exports = SucursalesController;