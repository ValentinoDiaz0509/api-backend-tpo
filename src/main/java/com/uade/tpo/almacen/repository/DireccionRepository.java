package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.Direccion;
import com.uade.tpo.almacen.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    List<Direccion> findByUsuario(Usuario usuario);
}
