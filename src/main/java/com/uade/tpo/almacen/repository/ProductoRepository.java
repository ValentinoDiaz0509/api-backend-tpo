package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.DetalleOrden;
import com.uade.tpo.almacen.entity.ItemCarrito;
import com.uade.tpo.almacen.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {}
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Integer> {}


    @Override
    @EntityGraph(attributePaths = {"categoria", "imagenes"})
    Page<Producto> findAll(Specification<Producto> spec, Pageable pageable);

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
