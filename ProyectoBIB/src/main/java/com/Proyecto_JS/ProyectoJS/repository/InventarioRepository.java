package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.Inventario;
import com.Proyecto_JS.ProyectoJS.entity.Libro;
import com.Proyecto_JS.ProyectoJS.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    // Método que necesita PrestamoService
    Optional<Inventario> findBySucursalIdAndLibroId(Long sucursalId, Long libroId);

    // Método que necesita PedidoService
    Optional<Inventario> findBySucursalAndLibro(Sucursal sucursal, Libro libro);

    // Métodos adicionales
    List<Inventario> findBySucursalId(Long sucursalId);
    
    List<Inventario> findByLibroId(Long libroId);
}
