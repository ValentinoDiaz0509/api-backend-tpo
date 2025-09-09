package com.uade.tpo.almacen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.almacen.controller.dto.CatalogoResponse;
import com.uade.tpo.almacen.controller.dto.ProductoDTO;
import com.uade.tpo.almacen.entity.dto.ProductoRequest;
import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.excepciones.ParametroFueraDeRangoException;
import com.uade.tpo.almacen.excepciones.ProductoDuplicateException;
import com.uade.tpo.almacen.excepciones.ProductoNotFoundException;
import com.uade.tpo.almacen.service.CategoriaService;
import com.uade.tpo.almacen.service.ProductoService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    @Autowired
    private CategoriaService categorias;

    @GetMapping
    public ResponseEntity<Page<ProductoDTO>> getProductos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) throws ProductoNotFoundException {

        int pageNum = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 200 : size;
        if (pageNum < 0 || pageSize < 1) {
            throw new ParametroFueraDeRangoException("Los parámetros de paginación deben ser mayores a 0");
        }

        Page<Producto> productos = productoService.filtrarProductos(
                nombre, marca, (categoriaId != null ? categoriaId.intValue() : null),
                precioMin, precioMax, PageRequest.of(pageNum, pageSize));

        var list = productos.stream()
                .filter(p -> p.getStock() > p.getStockMinimo() && "activo".equalsIgnoreCase(p.getEstado()))
                .map(ProductoDTO::new)
                .collect(Collectors.toList());

        Page<ProductoDTO> productosDTO = new PageImpl<>(list, productos.getPageable(), list.size());

        if (productosDTO.isEmpty()) {
            throw new ProductoNotFoundException("No hay productos que coincidan con los filtros");
        }
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) throws ProductoNotFoundException {
        if (id < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        Optional<Producto> result = productoService.getProductoById(id);
        if (result.isPresent()) {
            Producto p = result.get();
            if (p.getStock() > p.getStockMinimo() && "activo".equalsIgnoreCase(p.getEstado())) {
                return ResponseEntity.ok(new ProductoDTO(p));
            }
        }
        throw new ProductoNotFoundException("No se encontró el producto con id: " + id);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ProductoDTO> getProductoByCategory(@PathVariable Long categoriaId)
            throws ProductoNotFoundException {
        if (categoriaId < 1) {
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        }
        Optional<Categoria> categoriaOptional = categorias.getCategoriaById(categoriaId);
        if (categoriaOptional.isPresent()) {
            Optional<Producto> producto = productoService.getProductoByCategory(categoriaOptional.get());
            if (producto.isPresent()) {
                Producto p = producto.get();
                if (p.getStock() > p.getStockMinimo() && "activo".equalsIgnoreCase(p.getEstado())) {
                    return ResponseEntity.ok(new ProductoDTO(p));
                }
            }
        }
        throw new ProductoNotFoundException("No se encontró el producto con categoría: " + categoriaId);
    }

    @GetMapping("/nombre/{nombreProducto}")
    public ResponseEntity<ProductoDTO> getProductoByName(@PathVariable String nombreProducto)
            throws ProductoNotFoundException {
        if (nombreProducto == null || nombreProducto.isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        Optional<Producto> result = productoService.getProductoByName(nombreProducto);
        if (result.isPresent()) {
            Producto p = result.get();
            if (p.getStock() > p.getStockMinimo() && "activo".equalsIgnoreCase(p.getEstado())) {
                return ResponseEntity.ok(new ProductoDTO(p));
            }
        }
        throw new ProductoNotFoundException("No se encontró el producto con nombre: " + nombreProducto);
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<ProductoDTO> getProductoByMarca(@PathVariable String marca)
            throws ProductoNotFoundException {
        if (marca == null || marca.isEmpty()) {
            throw new ParametroFueraDeRangoException("La marca no puede ser nula o vacía");
        }
        Optional<Producto> result = productoService.getProductoByMarca(marca);
        if (result.isPresent()) {
            Producto p = result.get();
            if (p.getStock() > p.getStockMinimo() && "activo".equalsIgnoreCase(p.getEstado())) {
                return ResponseEntity.ok(new ProductoDTO(p));
            }
        }
        throw new ProductoNotFoundException("No se encontró el producto con marca: " + marca);
    }

    @GetMapping("/precio")
    public ResponseEntity<ProductoDTO> getProductoByPrecio(
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) BigDecimal precioMin) throws ProductoNotFoundException {

        Optional<Producto> result = Optional.empty();

        if (precioMax == null && precioMin == null) {
            throw new ParametroFueraDeRangoException("Ambos precios no pueden ser nulos");
        } else if (precioMax != null && precioMin != null && precioMax.compareTo(precioMin) < 0) {
            throw new ParametroFueraDeRangoException("El precio máximo debe ser mayor al mínimo");
        } else if (precioMax == null) {
            result = productoService.getProductoByPrecioMinimo(precioMin);
        } else if (precioMin == null) {
            result = productoService.getProductoByPrecioMaximo(precioMax);
        } else {
            result = productoService.getProductoByPrecio(precioMax, precioMin);
        }

        if (result.isPresent()) {
            Producto p = result.get();
            if (p.getStock() > p.getStockMinimo() && "activo".equalsIgnoreCase(p.getEstado())) {
                return ResponseEntity.ok(new ProductoDTO(p));
            }
        }
        throw new ProductoNotFoundException(
                "No se encontraron productos con precio máximo: " + precioMax + " y mínimo: " + precioMin);
    }

    @GetMapping("/catalogo")
    public ResponseEntity<Page<CatalogoResponse>> getCatalogo(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) throws ProductoNotFoundException {

        int pageNum = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 200 : size;
        if (pageNum < 0 || pageSize < 1) {
            throw new ParametroFueraDeRangoException("Los parámetros de paginación deben ser mayores a 0");
        }

        Page<Producto> productos = productoService.filtrarProductos(
                nombre, marca, (categoriaId != null ? categoriaId.intValue() : null),
                precioMin, precioMax, PageRequest.of(pageNum, pageSize));

        if (productos.isEmpty()) {
            throw new ProductoNotFoundException("No hay productos que coincidan con los filtros");
        }

        var list = productos.stream()
                .filter(p -> p.getStock() > p.getStockMinimo() && "activo".equals(p.getEstado()))
                .map(CatalogoResponse::new)
                .collect(Collectors.toList());

        Page<CatalogoResponse> catalogoResponse =
                new PageImpl<>(list, productos.getPageable(), list.size());

        if (catalogoResponse.isEmpty()) {
            throw new ProductoNotFoundException("No hay productos cargados");
        }
        return ResponseEntity.ok(catalogoResponse);
    }

    @PostMapping
    public ResponseEntity<Object> createProducto(@RequestBody ProductoRequest producto)
            throws ProductoDuplicateException, ParametroFueraDeRangoException {

        if (producto.getCategoria_id() == null || producto.getCategoria_id() < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }

        categorias.getCategoriaById(producto.getCategoria_id())
                .orElseThrow(() -> new ParametroFueraDeRangoException("La categoría no existe"));

        Producto result = productoService.createProducto(producto);
        return ResponseEntity.created(URI.create("/productos/" + result.getId()))
                .body(new ProductoDTO(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProducto(@PathVariable Long id, @RequestBody ProductoRequest productoRequest)
            throws ProductoNotFoundException {

        if (productoRequest.getCategoria_id() == null || productoRequest.getCategoria_id() < 1) {
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        }

        categorias.getCategoriaById(productoRequest.getCategoria_id())
                .orElseThrow(() -> new ParametroFueraDeRangoException("La categoría no existe"));

        Producto result = productoService.updateProducto(id, productoRequest);
        return ResponseEntity.ok(new ProductoDTO(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProducto(@PathVariable Long id) throws ProductoNotFoundException {
        if (id < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        productoService.getProductoById(id)
                .orElseThrow(() -> new ProductoNotFoundException("No se encontró el producto con id: " + id));
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
