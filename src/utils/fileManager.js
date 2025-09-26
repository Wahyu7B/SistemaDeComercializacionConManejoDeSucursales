const fs = require('fs').promises;
const path = require('path');

class FileManager {
    constructor(filename) {
        this.filepath = path.join(__dirname, '../data', filename);
    }

    async readData() {
        try {
            const data = await fs.readFile(this.filepath, 'utf8');
            return JSON.parse(data);
        } catch (error) {
            // Si el archivo no existe, crear estructura básica
            if (error.code === 'ENOENT') {
                const baseStructure = { [this.getCollectionName()]: [] };
                await this.writeData(baseStructure);
                return baseStructure;
            }
            throw error;
        }
    }

    async writeData(data) {
        await fs.writeFile(this.filepath, JSON.stringify(data, null, 2));
    }

    getCollectionName() {
        // Extraer nombre de colección del nombre del archivo
        return path.basename(this.filepath, '.json');
    }
}

module.exports = FileManager;