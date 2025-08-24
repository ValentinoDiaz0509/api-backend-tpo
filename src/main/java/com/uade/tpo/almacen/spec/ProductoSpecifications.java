package com.uade.tpo.almacen.spec;

import com.uade.tpo.almacen.entity.Producto;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductoSpecifications {

    public static Specification<Producto> nombreLike(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null; // <- devuelve algo en este camino
        }
        final String pattern = "%" + nombre.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("nombre")), pattern);
    }

    public static Specification<Producto> marcaLike(String marca) {
        if (marca == null || marca.trim().isEmpty()) {
            return null;
        }
        final String pattern = "%" + marca.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("marca")), pattern);
    }

    public static Specification<Producto> categoriaIdEquals(Integer categoriaId) {
        if (categoriaId == null || categoriaId <= 0) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Producto> precioMin(BigDecimal min) {
        if (min == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("precio"), min);
    }

    public static Specification<Producto> precioMax(BigDecimal max) {
        if (max == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("precio"), max);
    }

    public static Specification<Producto> activoConStock() {
        return (root, query, cb) -> cb.and(
                cb.equal(cb.lower(root.get("estado")), "activo"),
                cb.greaterThan(root.get("stock"), root.get("stockMinimo"))
        );
    }
}
