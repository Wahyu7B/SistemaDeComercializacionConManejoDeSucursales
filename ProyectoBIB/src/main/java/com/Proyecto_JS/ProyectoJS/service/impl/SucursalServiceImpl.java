package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.entity.Sucursal;
import com.Proyecto_JS.ProyectoJS.repository.SucursalRepository;
import com.Proyecto_JS.ProyectoJS.service.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SucursalServiceImpl implements SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    @Override
    public List<Sucursal> obtenerTodasLasSucursales() {
        return sucursalRepository.findAll();
    }
}