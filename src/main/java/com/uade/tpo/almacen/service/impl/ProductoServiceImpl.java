package com.uade.tpo.almacen.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.Imagen;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.entity.dto.ProductoRequest;
import com.uade.tpo.almacen.repository.CategoriaRepository;
import com.uade.tpo.almacen.repository.ProductoRepository;
import com.uade.tpo.almacen.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Page<Producto> filtrarProductos(String nombre, String marca, Integer categoriaId,
                                           BigDecimal precioMin, BigDecimal precioMax, Pageable pageable) {
        return productoRepository.filtrar(nombre, marca, categoriaId, precioMin, precioMax, pageable);
    }

    @Override
    public Optional<Producto> getProductoById(int id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> getProductoByName(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    @Override
    public Optional<Producto> getProductoByCategory(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public Optional<Producto> getProductoByMarca(String marca) {
        return productoRepository.findByMarca(marca);
    }

    @Override
    public Optional<Producto> getProductoByPrecioMaximo(BigDecimal precioMax) {
        return productoRepository.findFirstByPrecioLessThanEqual(precioMax);
    }

    @Override
    public Optional<Producto> getProductoByPrecioMinimo(BigDecimal precioMin) {
        return productoRepository.findFirstByPrecioGreaterThanEqual(precioMin);
    }

    @Override
    public Optional<Producto> getProductoByPrecio(BigDecimal precioMax, BigDecimal precioMin) {
        return productoRepository.findFirstByPrecioBetween(precioMin, precioMax);
    }

    @Override
    public Producto createProducto(ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoria_id()).orElseThrow();
        Producto producto = mapearProducto(new Producto(), request);
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    @Override
    public Producto updateProducto(int id, ProductoRequest request) {
        Producto existente = productoRepository.findById(id).orElseThrow();
        Categoria categoria = categoriaRepository.findById(request.getCategoria_id()).orElseThrow();
        existente.getImagenes().clear();
        mapearProducto(existente, request);
        existente.setCategoria(categoria);
        return productoRepository.save(existente);
    }

    @Override
    public void deleteProducto(int id) {
        productoRepository.deleteById(id);
    }

    private Producto mapearProducto(Producto producto, ProductoRequest request) {
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setMarca(request.getMarca());
        producto.setPrecio(request.getPrecio());
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setDescuento(request.getDescuento());
        producto.setStock(request.getStock());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setVentasTotales(request.getVentasTotales());
        producto.setEstado(request.getEstado());
        for (String img : request.getImagenes()) {
            Imagen imagen = Imagen.builder().imagen(img).producto(producto).build();
            producto.getImagenes().add(imagen);
        }
        return producto;
    }
}
