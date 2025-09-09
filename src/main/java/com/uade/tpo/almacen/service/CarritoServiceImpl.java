package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.*;
import com.uade.tpo.almacen.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProductoRepository productoRepo;
    private final ItemCarritoRepository itemRepo;

    public CarritoServiceImpl(CarritoRepository carritoRepo,
                              UsuarioRepository usuarioRepo,
                              ProductoRepository productoRepo,
                              ItemCarritoRepository itemRepo) {
        this.carritoRepo = carritoRepo;
        this.usuarioRepo = usuarioRepo;
        this.productoRepo = productoRepo;
        this.itemRepo = itemRepo;
    }

    @Override
    @Transactional
    public Carrito obtenerOCrearCarrito(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // ✅ Buscar cualquier carrito existente del usuario
        return carritoRepo.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito c = new Carrito();
                    c.setUsuario(usuario);
                    c.setEstado(EstadoCarrito.VACIO);
                    c.setFechaCreacion(LocalDateTime.now());
                    return carritoRepo.save(c);
                });
    }

    @Override
    @Transactional
    public Carrito agregarItem(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser > 0");

        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Optional<ItemCarrito> existente = carrito.getItemsCarrito().stream()
                .filter(i -> i.getProducto() != null && i.getProducto().getId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            itemRepo.save(item);
        } else {
            ItemCarrito item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(producto.getPrecio());
            itemRepo.save(item);
            carrito.getItemsCarrito().add(item);
        }

        // ✅ activar carrito si estaba vacío
        if (carrito.getEstado() == EstadoCarrito.VACIO) {
            carrito.setEstado(EstadoCarrito.ACTIVO);
            carrito.setFechaActivacion(LocalDateTime.now());
        }

        return carritoRepo.save(carrito);
    }

    @Override
    @Transactional
    public Carrito quitarItem(Long usuarioId, Long itemId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        carrito.getItemsCarrito().removeIf(i -> i.getId().equals(itemId));
        itemRepo.deleteById(itemId);

        if (carrito.getItemsCarrito().isEmpty()) {
            carrito.setEstado(EstadoCarrito.VACIO);
        }

        return carritoRepo.save(carrito);
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        itemRepo.deleteAll(carrito.getItemsCarrito());
        carrito.getItemsCarrito().clear();
        carrito.setEstado(EstadoCarrito.VACIO);

        carritoRepo.save(carrito);
    }
}

