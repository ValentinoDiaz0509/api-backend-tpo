package com.uade.tpo.almacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.almacen.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
