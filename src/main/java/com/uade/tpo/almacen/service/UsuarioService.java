package com.uade.tpo.almacen.service;

import java.util.List;
import java.util.Optional;

import com.uade.tpo.almacen.entity.Usuario;

public interface UsuarioService {
    List<Usuario> getAllUsuarios();
    Optional<Usuario> getUsuarioById(int id);
    Optional<Usuario> getUsuarioByEmail(String email);
    Optional<Usuario> getUsuarioByUsername(String username);
    Usuario createOrUpdateUsuario(Usuario usuario);
    void deleteUsuarioById(int id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Usuario> getUsuariosByRol(String rol);
}
