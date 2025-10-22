// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/impl/UsuarioServiceImpl.java
package com.Proyecto_JS.ProyectoJS.service.impl;

import com.Proyecto_JS.ProyectoJS.dto.UsuarioRegistroDTO;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import com.Proyecto_JS.ProyectoJS.exception.RecursoNoEncontradoException;
import com.Proyecto_JS.ProyectoJS.repository.UsuarioRepository;
import com.Proyecto_JS.ProyectoJS.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario guardar(UsuarioRegistroDTO registroDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRol(Usuario.Rol.CLIENTE);
        usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setUpdatedAt(LocalDateTime.now());
        
        // ✅ LÍNEA CORREGIDA: Faltaba devolver el usuario guardado.
        return usuarioRepository.save(usuario); 
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario cambiarRol(Long usuarioId, Usuario.Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));
        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario cambiarEstado(Long usuarioId, Usuario.EstadoUsuario nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));
        usuario.setEstado(nuevoEstado);
        return usuarioRepository.save(usuario);
    }
}