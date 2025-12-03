// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/service/UsuarioService.java
package com.Proyecto_JS.ProyectoJS.service;

import com.Proyecto_JS.ProyectoJS.dto.UsuarioRegistroDTO;
import com.Proyecto_JS.ProyectoJS.entity.Usuario;
import java.util.List;

public interface UsuarioService {

    Usuario guardar(UsuarioRegistroDTO registroDTO);

    List<Usuario> obtenerTodosLosUsuarios();

    Usuario cambiarRol(Long usuarioId, Usuario.Rol nuevoRol);

    Usuario cambiarEstado(Long usuarioId, Usuario.EstadoUsuario nuevoEstado);
    void eliminarUsuario(Long usuarioId);
}