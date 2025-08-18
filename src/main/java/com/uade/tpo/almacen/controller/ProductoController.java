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

=======
@RequestMapping("productos")
=======
 main
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
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) throws ProductoNotFoundException {

        int pageNum = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 200 : size;
        if (pageNum < 0 || pageSize < 1) {
            throw new ParametroFueraDeRangoException("Los parámetros de paginación deben ser mayores a 0");
        }

        Page<Producto> productos = productoService.filtrarProductos(
                nombre, marca, categoriaId, precioMin, precioMax, PageRequest.of(pageNum, pageSize));

        if (productos.isEmpty()) {
            throw new ProductoNotFoundException("No hay productos que coincidan con los filtros");
        }


        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::new);
=======
        var list = productos.stream()
                .filter(p -> p.getStock() > p.getStockMinimo() && "activo".equals(p.getEstado()))
                .map(ProductoDTO::new)
                .collect(Collectors.toList());

        Page<ProductoDTO> productosDTO = new PageImpl<>(list, productos.getPageable(), list.size());

        if (productosDTO.isEmpty()) {
            throw new ProductoNotFoundException("No hay productos cargados");
        }
=======
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::new);
main
 main
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable int id) throws ProductoNotFoundException {
        if (id < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        Optional<Producto> result = productoService.getProductoById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(new ProductoDTO(result.get()));
        }
        throw new ProductoNotFoundException("No se encontró el producto con id: " + id);
    }

    @GetMapping("/nombre/{nombreProducto}")
    public ResponseEntity<ProductoDTO> getProductoByName(@PathVariable String nombreProducto)
            throws ProductoNotFoundException {
        if (nombreProducto == null || nombreProducto.isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        Optional<Producto> result = productoService.getProductoByName(nombreProducto);
        if (result.isPresent()) {
            return ResponseEntity.ok(new ProductoDTO(result.get()));
        }
        throw new ProductoNotFoundException("No se encontró el producto con nombre: " + nombreProducto);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ProductoDTO> getProductoByCategory(@PathVariable int categoriaId)
            throws ProductoNotFoundException {
        if (categoriaId < 1) {
            throw new ParametroFueraDeRangoException("El id de la categoría debe ser mayor a 0");
        }
        Optional<Categoria> categoriaOptional = categorias.getCategoriaById(categoriaId);
        if (categoriaOptional.isPresent()) {
            Optional<Producto> producto = productoService.getProductoByCategory(categoriaOptional.get());
            if (producto.isPresent()) {
                return ResponseEntity.ok(new ProductoDTO(producto.get()));
            }
        }
        throw new ProductoNotFoundException("No se encontró el producto con categoría: " + categoriaId);
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<ProductoDTO> getProductoByMarca(@PathVariable String marca)
            throws ProductoNotFoundException {
        if (marca == null || marca.isEmpty()) {
            throw new ParametroFueraDeRangoException("La marca no puede ser nula o vacía");
        }
        Optional<Producto> result = productoService.getProductoByMarca(marca);
        if (result.isPresent()) {
            return ResponseEntity.ok(new ProductoDTO(result.get()));
        }
        throw new ProductoNotFoundException("No se encontró el producto con marca: " + marca);
    }


    // Versión con query params: /producto/precio?precioMax=...&precioMin=...
=======
    // Versión con query params: /productos/precio?precioMax=...&precioMin=...
=======
    // Versión con query params: /producto/precio?precioMax=...&precioMin=...
 main
 main
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
            return ResponseEntity.ok(new ProductoDTO(result.get()));
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
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) throws ProductoNotFoundException {

        int pageNum = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 200 : size;
        if (pageNum < 0 || pageSize < 1) {
            throw new ParametroFueraDeRangoException("Los parámetros de paginación deben ser mayores a 0");
        }

        Page<Producto> productos = productoService.filtrarProductos(
                nombre, marca, categoriaId, precioMin, precioMax, PageRequest.of(pageNum, pageSize));

        if (productos.isEmpty()) {
            throw new ProductoNotFoundException("No hay productos que coincidan con los filtros");
        }

        var list = productos.stream()
                .filter(p -> p.getStock() > p.getStockMinimo() && "activo".equals(p.getEstado()))
                .map(CatalogoResponse::new)
                .collect(Collectors.toList());

        Page<CatalogoResponse> catalogoResponse =
                new PageImpl<>(list, productos.getPageable(), list.size()); // total consistente con el filtro

        if (catalogoResponse.isEmpty()) {
            throw new ProductoNotFoundException("No hay productos cargados");
        }
        return ResponseEntity.ok(catalogoResponse);
    }

    @PostMapping
    public ResponseEntity<Object> createProducto(@RequestBody ProductoRequest producto)
            throws ProductoDuplicateException, ParametroFueraDeRangoException {

        if (producto.getCategoria_id() < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParametroFueraDeRangoException("El precio no puede ser nulo o menor a 0");
        }
        if (producto.getDescripcion() == null || producto.getDescripcion().isEmpty()) {
            throw new ParametroFueraDeRangoException("La descripción no puede ser nula o vacía");
        }
        if (producto.getStock() < 0) {
            throw new ParametroFueraDeRangoException("El stock no puede ser menor a 0");
        }
        if (producto.getStockMinimo() < 0) {
            throw new ParametroFueraDeRangoException("El stock mínimo no puede ser menor a 0");
        }
        if (producto.getDescuento() == null
                || producto.getDescuento().compareTo(BigDecimal.ZERO) < 0
                || producto.getDescuento().compareTo(new BigDecimal("100")) > 0) {
            throw new ParametroFueraDeRangoException("El descuento debe estar entre 0 y 100");
        }
        if (producto.getVentasTotales() < 0) {
            throw new ParametroFueraDeRangoException("Las ventas totales no pueden ser menores a 0");
        }
        if (producto.getImagenes().size() > 10) {
            throw new ParametroFueraDeRangoException("No se pueden agregar más de 10 imagenes");
        }

        categorias.getCategoriaById(producto.getCategoria_id())
                .orElseThrow(() -> new ParametroFueraDeRangoException("La categoría no existe"));

        Producto result = productoService.createProducto(producto);
        return ResponseEntity.created(URI.create("/productos/" + result.getId()))
                .body(new ProductoDTO(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProducto(@PathVariable int id, @RequestBody ProductoRequest productoRequest)
            throws ProductoNotFoundException {

        if (productoRequest.getCategoria_id() < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        if (productoRequest.getNombre() == null || productoRequest.getNombre().isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre del producto no puede ser nulo o vacío");
        }
        if (productoRequest.getPrecio() == null || productoRequest.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParametroFueraDeRangoException("El precio no puede ser nulo o menor a 0");
        }
        if (productoRequest.getDescripcion() == null || productoRequest.getDescripcion().isEmpty()) {
            throw new ParametroFueraDeRangoException("La descripción no puede ser nula o vacía");
        }
        if (productoRequest.getStock() < 0) {
            throw new ParametroFueraDeRangoException("El stock no puede ser menor a 0");
        }
        if (productoRequest.getStockMinimo() < 0) {
            throw new ParametroFueraDeRangoException("El stock mínimo no puede ser menor a 0");
        }
        if (productoRequest.getDescuento() == null
                || productoRequest.getDescuento().compareTo(BigDecimal.ZERO) < 0
                || productoRequest.getDescuento().compareTo(new BigDecimal("100")) > 0) {
            throw new ParametroFueraDeRangoException("El descuento debe estar entre 0 y 100");
        }
        if (productoRequest.getVentasTotales() < 0) {
            throw new ParametroFueraDeRangoException("Las ventas totales no pueden ser menores a 0");
        }
        if (productoRequest.getImagenes().size() > 10) {
            throw new ParametroFueraDeRangoException("No se pueden agregar más de 10 imagenes");
        }

        categorias.getCategoriaById(productoRequest.getCategoria_id())
                .orElseThrow(() -> new ParametroFueraDeRangoException("La categoría no existe"));

        Producto result = productoService.updateProducto(id, productoRequest);
        return ResponseEntity.ok(new ProductoDTO(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProducto(@PathVariable int id) throws ProductoNotFoundException {
        if (id < 1) {
            throw new ParametroFueraDeRangoException("El id del producto debe ser mayor a 0");
        }
        productoService.getProductoById(id)
                .orElseThrow(() -> new ProductoNotFoundException("No se encontró el producto con id: " + id));
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
