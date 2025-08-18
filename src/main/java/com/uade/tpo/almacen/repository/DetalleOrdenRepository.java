package com.uade.tpo.almacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.almacen.entity.DetalleOrden;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Integer> {
}
