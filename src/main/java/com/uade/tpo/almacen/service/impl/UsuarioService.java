package com.uade.tpo.almacen.service.impl;

import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.security.crypto.password.PasswordEncoder; // <- si usás Spring Security

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // private final PasswordEncoder passwordEncoder; // opcional

    public UsuarioService(UsuarioRepository usuarioRepository
                      ) {
        this.usuarioRepository = usuarioRepository;
    }

   
    @Transactional(readOnly = true)
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Usuario> getAllUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioById(int id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioByIdOrThrow(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosByRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    
    @Transactional
    public Usuario createUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }

       

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateUsuario(Usuario cambios) {
        if (cambios.getId() == 0) {
            throw new IllegalArgumentException("ID inválido para actualización.");
        }

        Usuario existente = usuarioRepository.findById(cambios.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + cambios.getId()));

        if (cambios.getUsername() != null && !cambios.getUsername().equals(existente.getUsername())) {
            if (usuarioRepository.existsByUsername(cambios.getUsername())) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
            }
            existente.setUsername(cambios.getUsername());
        }
        if (cambios.getEmail() != null && !cambios.getEmail().equals(existente.getEmail())) {
            if (usuarioRepository.existsByEmail(cambios.getEmail())) {
                throw new IllegalArgumentException("El email ya está en uso.");
            }
            existente.setEmail(cambios.getEmail());
        }

        if (cambios.getPassword() != null && !cambios.getPassword().isBlank()) {
            existente.setPassword(cambios.getPassword());
        }
        if (cambios.getNombre() != null) existente.setNombre(cambios.getNombre());
        if (cambios.getApellido() != null) existente.setApellido(cambios.getApellido());
        if (cambios.getRol() != null) existente.setRol(cambios.getRol());

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void deleteUsuarioById(int id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario createOrUpdateUsuario(Usuario usuario) {
        if (usuario.getId() == 0) {
            return createUsuario(usuario);
        }
        return updateUsuario(usuario);
    }
}
