package com.uade.tpo.almacen.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.almacen.entity.Carrito;
import com.uade.tpo.almacen.entity.ItemCarrito;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.repository.CarritoRepository;
import com.uade.tpo.almacen.repository.ItemCarritoRepository;
import com.uade.tpo.almacen.repository.ProductoRepository;
import com.uade.tpo.almacen.repository.UsuarioRepository;
import com.uade.tpo.almacen.service.CarritoService;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ItemCarritoRepository itemCarritoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoRepository productoRepository,
                              ItemCarritoRepository itemCarritoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
    }

    @Override
    public Carrito obtenerOCrearCarrito(int usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    return carritoRepository.save(nuevo);
                });
    }

    @Override
    public Carrito agregarItem(int usuarioId, int productoId, int cantidad) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        Optional<ItemCarrito> existente = carrito.getItemsCarrito().stream()
                .filter(i -> i.getProducto().getId() == productoId)
                .findFirst();
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            ItemCarrito item = ItemCarrito.builder()
                    .producto(producto)
                    .carrito(carrito)
                    .cantidad(cantidad)
                    .precioUnitario(producto.getPrecio())
                    .subtotal(BigDecimal.ZERO)
                    .build();
            carrito.getItemsCarrito().add(item);
        }
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito quitarItem(int usuarioId, int itemId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItemsCarrito().removeIf(i -> i.getId() == itemId);
        return carritoRepository.save(carrito);
    }

    @Override
    public void vaciarCarrito(int usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.vaciar();
        carritoRepository.save(carrito);
    }
}
