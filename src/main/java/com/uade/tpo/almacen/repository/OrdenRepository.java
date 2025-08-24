package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.Orden;
import com.uade.tpo.almacen.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
}
