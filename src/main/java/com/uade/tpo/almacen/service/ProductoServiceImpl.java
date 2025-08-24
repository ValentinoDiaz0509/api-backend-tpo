package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.HistorialPrecio;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.entity.Imagen;
import com.uade.tpo.almacen.entity.dto.ProductoRequest; // <- DTO en entity.dto
import com.uade.tpo.almacen.repository.CategoriaRepository;
import com.uade.tpo.almacen.repository.HistorialPrecioRepository;
import com.uade.tpo.almacen.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.uade.tpo.almacen.spec.ProductoSpecifications.*; 

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepo;
    private final CategoriaRepository categoriaRepo;
    private final HistorialPrecioRepository historialRepo;

    public ProductoServiceImpl(ProductoRepository productoRepo,
                               CategoriaRepository categoriaRepo,
                               HistorialPrecioRepository historialRepo) {
        this.productoRepo = productoRepo;
        this.categoriaRepo = categoriaRepo;
        this.historialRepo = historialRepo;
    }

    // ==========================
    // Listado / Filtros paginados
    // ==========================
    @Override
    public Page<Producto> filtrarProductos(String nombre,
                                           String marca,
                                           Integer categoriaId,
                                           BigDecimal precioMin,
                                           BigDecimal precioMax,
                                           Pageable pageable) {

        Specification<Producto> spec = Specification
                .where(nombreLike(nombre))
                .and(marcaLike(marca))
                .and(categoriaIdEquals(categoriaId))
                .and(precioMin(precioMin))
                .and(precioMax(precioMax));

        return productoRepo.findAll(spec, pageable);
    }

    // ==========================
    // Finders usados por el controller
    // ==========================
    @Override
    public Optional<Producto> getProductoById(int id) {
        return productoRepo.findById(id);
    }

    @Override
    public Optional<Producto> getProductoByName(String nombreProducto) {
        return productoRepo.findFirstByNombreIgnoreCase(nombreProducto);
    }

    @Override
    public Optional<Producto> getProductoByMarca(String marca) {
        return productoRepo.findFirstByMarcaIgnoreCase(marca);
    }

    @Override
    public Optional<Producto> getProductoByCategory(Categoria categoria) {
        return productoRepo.findFirstByCategoria(categoria);
    }

    @Override
    public Optional<Producto> getProductoByPrecioMaximo(BigDecimal precioMax) {
        return productoRepo.findFirstByPrecioLessThanEqual(precioMax);
    }

    @Override
    public Optional<Producto> getProductoByPrecioMinimo(BigDecimal precioMin) {
        return productoRepo.findFirstByPrecioGreaterThanEqual(precioMin);
    }

    @Override
    public Optional<Producto> getProductoByPrecio(BigDecimal precioMax, BigDecimal precioMin) {
        if (precioMax == null && precioMin == null) return Optional.empty();
        BigDecimal min = (precioMin == null) ? BigDecimal.ZERO : precioMin;
        BigDecimal max = (precioMax == null) ? min : precioMax;
        if (max.compareTo(min) < 0) { BigDecimal t = min; min = max; max = t; }
        return productoRepo.findFirstByPrecioBetween(min, max);
    }

    // ==========================
    // CRUD con DTO
    // ==========================
    @Override
    @Transactional
    public Producto createProducto(ProductoRequest req) {
        Categoria cat = categoriaRepo.findById(req.getCategoria_id())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + req.getCategoria_id()));

        if (productoRepo.existsByNombreAndDescripcionAndMarcaAndCategoria(
                req.getNombre(), req.getDescripcion(), req.getMarca(), cat)) {
            throw new IllegalArgumentException("Ya existe un producto con mismos datos en esa categoría");
        }

        Producto p = new Producto();
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setMarca(req.getMarca());
        p.setPrecio(req.getPrecio());

        // estos campos deben existir en la entidad Producto; si no, quitarlos
        p.setUnidadMedida(req.getUnidadMedida());
        p.setDescuento(req.getDescuento());
        p.setStock(req.getStock());
        p.setStockMinimo(req.getStockMinimo());
        p.setVentasTotales(req.getVentasTotales());
        p.setEstado(req.getEstado());

        p.setCategoria(cat);

        if (req.getImagenes() != null) {
            for (String imgStr : req.getImagenes()) {
                Imagen img = new Imagen();
                img.setImagen(imgStr);
                img.setProducto(p);
                p.getImagenes().add(img);
            }
        }

        return productoRepo.save(p);
    }

    @Override
    @Transactional
    public Producto updateProducto(int id, ProductoRequest req) {
        Producto p = productoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

        // Historial de precio si cambia
        if (req.getPrecio() != null) {
            if (p.getPrecio() == null || req.getPrecio().compareTo(p.getPrecio()) != 0) {
                if (p.getPrecio() != null) {
                    historialRepo.save(new HistorialPrecio(p, p.getPrecio(), req.getPrecio()));
                }
                p.setPrecio(req.getPrecio());
            }
        }

        if (req.getNombre() != null) p.setNombre(req.getNombre());
        if (req.getDescripcion() != null) p.setDescripcion(req.getDescripcion());
        if (req.getMarca() != null) p.setMarca(req.getMarca());
        if (req.getUnidadMedida() != null) p.setUnidadMedida(req.getUnidadMedida());
        if (req.getDescuento() != null) p.setDescuento(req.getDescuento());
        if (req.getStock() != null) p.setStock(req.getStock());
        if (req.getStockMinimo() != null) p.setStockMinimo(req.getStockMinimo());
        if (req.getVentasTotales() != null) p.setVentasTotales(req.getVentasTotales());
        if (req.getEstado() != null) p.setEstado(req.getEstado());

        if (req.getCategoria_id() > 0) {
            Categoria cat = categoriaRepo.findById(req.getCategoria_id())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + req.getCategoria_id()));
            p.setCategoria(cat);
        }

        if (req.getImagenes() != null) {
            p.getImagenes().clear();
            for (String imgStr : req.getImagenes()) {
                Imagen img = new Imagen();
                img.setImagen(imgStr);
                img.setProducto(p);
                p.getImagenes().add(img);
            }
        }

        return productoRepo.save(p);
    }

    @Override
    @Transactional
    public void deleteProducto(int id) {
        productoRepo.deleteById(id);
    }

    // ==========================
    // (Legacy)
    // ==========================
    @Override
    public List<Producto> listar() { return productoRepo.findAll(); }

    @Override
    public Producto obtener(int id) {
        return productoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    @Override
    @Transactional
    public Producto crear(Producto p, int categoriaId) {
        Categoria cat = categoriaRepo.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
        if (productoRepo.existsByNombreAndDescripcionAndMarcaAndCategoria(
                p.getNombre(), p.getDescripcion(), p.getMarca(), cat)) {
            throw new IllegalArgumentException("Ya existe un producto con mismos datos en esa categoría");
        }
        p.setCategoria(cat);

        if (p.getImagenes() != null) {
            p.getImagenes().forEach(img -> img.setProducto(p));
        }

        return productoRepo.save(p);
    }

    @Override
    @Transactional
    public Producto actualizar(int id, Producto cambios, Integer categoriaId) {
        Producto p = obtener(id);

        BigDecimal nuevoPrecio = cambios.getPrecio();
        if (nuevoPrecio != null) {
            if (p.getPrecio() == null || !nuevoPrecio.equals(p.getPrecio())) {
                if (p.getPrecio() != null) {
                    historialRepo.save(new HistorialPrecio(p, p.getPrecio(), nuevoPrecio));
                }
                p.setPrecio(nuevoPrecio);
            }
        }

        if (cambios.getNombre() != null) p.setNombre(cambios.getNombre());
        if (cambios.getDescripcion() != null) p.setDescripcion(cambios.getDescripcion());
        if (cambios.getMarca() != null) p.setMarca(cambios.getMarca());
        if (cambios.getUnidadMedida() != null) p.setUnidadMedida(cambios.getUnidadMedida());
        if (cambios.getDescuento() != null) p.setDescuento(cambios.getDescuento());
        if (cambios.getStock() != null) p.setStock(cambios.getStock());
        if (cambios.getStockMinimo() != null) p.setStockMinimo(cambios.getStockMinimo());
        if (cambios.getEstado() != null) p.setEstado(cambios.getEstado());
        if (cambios.getVentasTotales() != null) p.setVentasTotales(cambios.getVentasTotales());

        if (categoriaId != null) {
            Categoria cat = categoriaRepo.findById(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
            p.setCategoria(cat);
        }

        if (cambios.getImagenes() != null) {
            p.getImagenes().clear();
            for (Imagen img : cambios.getImagenes()) {
                img.setProducto(p);
                p.getImagenes().add(img);
            }
        }
        return productoRepo.save(p);
    }

    @Override
    public void eliminar(int id) {
        productoRepo.deleteById(id);
    }
}
