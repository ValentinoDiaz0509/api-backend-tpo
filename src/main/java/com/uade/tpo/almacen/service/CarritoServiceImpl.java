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

    @Override @Transactional
    public Carrito obtenerOCrearCarrito(int usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return carritoRepo.findByUsuarioIdAndEstadoConItems(usuarioId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {
                    Carrito c = new Carrito();
                    c.setUsuario(usuario);
                    c.setEstado(EstadoCarrito.ACTIVO);
                    c.setFechaActivacion(LocalDateTime.now());
                    return carritoRepo.save(c);
                });
    }

    @Override @Transactional
    public Carrito agregarItem(int usuarioId, int productoId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser > 0");
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Optional<ItemCarrito> existente = carrito.getItemsCarrito().stream()
                .filter(i -> i.getProducto().getId() == productoId)
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
            itemRepo.save(item);
            carrito.getItemsCarrito().add(item);
        }
        return carritoRepo.save(carrito);
    }

    @Override @Transactional
    public Carrito quitarItem(int usuarioId, int itemId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItemsCarrito().removeIf(i -> i.getId() == itemId);
        itemRepo.deleteById(itemId);
        return carritoRepo.save(carrito);
    }

    @Override @Transactional
    public void vaciarCarrito(int usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        itemRepo.deleteAll(carrito.getItemsCarrito());
        carrito.getItemsCarrito().clear();
        carritoRepo.save(carrito);
    }
}
