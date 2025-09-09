package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
