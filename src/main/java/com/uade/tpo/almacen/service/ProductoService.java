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

   
    Optional<Producto> getProductoById(Long id);

    Optional<Producto> getProductoByName(String nombreProducto);

    Optional<Producto> getProductoByMarca(String marca);

    Optional<Producto> getProductoByCategory(Categoria categoria);

    Optional<Producto> getProductoByPrecioMaximo(BigDecimal precioMax);

    Optional<Producto> getProductoByPrecioMinimo(BigDecimal precioMin);

    Optional<Producto> getProductoByPrecio(BigDecimal precioMax, BigDecimal precioMin);

    
    Producto createProducto(ProductoRequest req);

    Producto updateProducto(Long id, ProductoRequest req);

    void deleteProducto(Long id);

    
    List<Producto> listar();

    Producto obtener(Long id);

    Producto crear(Producto producto, Long categoriaId);

    Producto actualizar(Long id, Producto cambios, Long categoriaId);

    void eliminar(Long id);
}
