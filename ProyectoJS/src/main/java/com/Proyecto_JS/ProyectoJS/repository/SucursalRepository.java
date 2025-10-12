package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
}