package com.uade.tpo.almacen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.almacen.entity.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Integer> {
    List<Orden> findByUsuarioId(int usuarioId);
    Optional<Orden> findByIdAndUsuarioId(int ordenId, int usuarioId);
}
