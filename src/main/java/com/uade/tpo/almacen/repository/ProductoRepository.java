package com.uade.tpo.almacen.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findByNombre(String nombre);
    Optional<Producto> findByMarca(String marca);
    Optional<Producto> findByCategoria(Categoria categoria);
    Optional<Producto> findFirstByPrecioLessThanEqual(BigDecimal precioMax);
    Optional<Producto> findFirstByPrecioGreaterThanEqual(BigDecimal precioMin);
    Optional<Producto> findFirstByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    @Query("SELECT p FROM Producto p WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%',:nombre,'%'))) " +
            "AND (:marca IS NULL OR LOWER(p.marca) LIKE LOWER(CONCAT('%',:marca,'%'))) " +
            "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId) " +
            "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
            "AND (:precioMax IS NULL OR p.precio <= :precioMax)")
    Page<Producto> filtrar(@Param("nombre") String nombre,
                           @Param("marca") String marca,
                           @Param("categoriaId") Integer categoriaId,
                           @Param("precioMin") BigDecimal precioMin,
                           @Param("precioMax") BigDecimal precioMax,
                           Pageable pageable);
}
