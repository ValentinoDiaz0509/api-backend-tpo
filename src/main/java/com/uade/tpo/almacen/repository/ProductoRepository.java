package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer>, JpaSpecificationExecutor<Producto> {

    boolean existsByNombreAndDescripcionAndMarcaAndCategoria(
            String nombre,
            String descripcion,
            String marca,
            Categoria categoria);

           

    Optional<Producto> findFirstByNombreIgnoreCase(String nombre);
    Optional<Producto> findFirstByMarcaIgnoreCase(String marca);
    Optional<Producto> findFirstByCategoria(Categoria categoria);

    Optional<Producto> findFirstByPrecioLessThanEqual(BigDecimal precioMax);
    Optional<Producto> findFirstByPrecioGreaterThanEqual(BigDecimal precioMin);
    Optional<Producto> findFirstByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
}
