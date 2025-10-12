package com.Proyecto_JS.ProyectoJS.repository;

import com.Proyecto_JS.ProyectoJS.entity.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
}