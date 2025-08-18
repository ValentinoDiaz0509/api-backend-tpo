package com.uade.tpo.almacen.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.entity.dto.ProductoRequest;

public interface ProductoService {
    Page<Producto> filtrarProductos(String nombre, String marca, Integer categoriaId,
                                   BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    Optional<Producto> getProductoById(int id);
    Optional<Producto> getProductoByName(String nombre);
    Optional<Producto> getProductoByCategory(Categoria categoria);
    Optional<Producto> getProductoByMarca(String marca);
    Optional<Producto> getProductoByPrecioMaximo(BigDecimal precioMax);
    Optional<Producto> getProductoByPrecioMinimo(BigDecimal precioMin);
    Optional<Producto> getProductoByPrecio(BigDecimal precioMax, BigDecimal precioMin);
    Producto createProducto(ProductoRequest request);
    Producto updateProducto(int id, ProductoRequest request);
    void deleteProducto(int id);
}
