package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.entity.dto.ProductoRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    Page<Producto> filtrarProductos(String nombre,
                                    String marca,
                                    Integer categoriaId,
                                    BigDecimal precioMin,
                                    BigDecimal precioMax,
                                    Pageable pageable);

    Optional<Producto> getProductoById(int id);
    Optional<Producto> getProductoByName(String nombreProducto);
    Optional<Producto> getProductoByMarca(String marca);
    Optional<Producto> getProductoByCategory(Categoria categoria);
    Optional<Producto> getProductoByPrecioMaximo(BigDecimal precioMax);
    Optional<Producto> getProductoByPrecioMinimo(BigDecimal precioMin);
    Optional<Producto> getProductoByPrecio(BigDecimal precioMax, BigDecimal precioMin);

    Producto createProducto(ProductoRequest req);
    Producto updateProducto(int id, ProductoRequest req);
    void deleteProducto(int id);

    List<Producto> listar();
    Producto obtener(int id);
    Producto crear(Producto p, int categoriaId);
    Producto actualizar(int id, Producto cambios, Integer categoriaId);
    void eliminar(int id);
}
