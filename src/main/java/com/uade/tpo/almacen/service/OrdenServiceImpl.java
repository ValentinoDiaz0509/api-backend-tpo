package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.*;
import com.uade.tpo.almacen.entity.dto.ItemOrdenDTO;
import com.uade.tpo.almacen.entity.dto.OrdenResponseDTO;
import com.uade.tpo.almacen.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenServiceImpl implements OrdenService {

    private final CarritoRepository carritoRepo;
    private final DireccionRepository direccionRepo;
    private final ProductoRepository productoRepo;
    private final OrdenRepository ordenRepo;
    private final DetalleOrdenRepository detalleRepo;
    private final UsuarioRepository usuarioRepo;

    public OrdenServiceImpl(CarritoRepository carritoRepo,
                            DireccionRepository direccionRepo,
                            ProductoRepository productoRepo,
                            OrdenRepository ordenRepo,
                            DetalleOrdenRepository detalleRepo,
                            UsuarioRepository usuarioRepo) {
        this.carritoRepo = carritoRepo;
        this.direccionRepo = direccionRepo;
        this.productoRepo = productoRepo;
        this.ordenRepo = ordenRepo;
        this.detalleRepo = detalleRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    @Transactional
    public Orden finalizarCompra(Usuario usuario, Long direccionId) {
        Carrito carrito = carritoRepo
                .findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new IllegalArgumentException("No hay carrito activo para el usuario"));

        if (carrito.getItemsCarrito().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE.name());
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setTotal(BigDecimal.ZERO);

        // ✅ corregido: usamos Long directamente
        if (direccionId != null) {
            Direccion direccion = direccionRepo.findById(direccionId)
                    .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada"));
            orden.setDireccion(direccion);
        }

        orden = ordenRepo.save(orden);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemCarrito ic : carrito.getItemsCarrito()) {
            Producto p = productoRepo.findById(ic.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (p.getStock() < ic.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para " + p.getNombre());
            }

            BigDecimal precioUnit = p.getPrecio();
            if (p.getDescuento() != null && p.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal porc = p.getDescuento().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                precioUnit = p.getPrecio().multiply(BigDecimal.ONE.subtract(porc))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal itemSubtotal = precioUnit
                    .multiply(BigDecimal.valueOf(ic.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(itemSubtotal);

            DetalleOrden det = DetalleOrden.builder()
                    .cantidad(ic.getCantidad())
                    .precioUnitario(precioUnit)
                    .subtotal(itemSubtotal)
                    .orden(orden)
                    .producto(p)
                    .build();
            detalleRepo.save(det);

            p.setStock(p.getStock() - ic.getCantidad());
            p.setVentasTotales(p.getVentasTotales() + ic.getCantidad());
            productoRepo.save(p);
        }

        orden.setTotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        orden = ordenRepo.save(orden);

        carrito.getItemsCarrito().clear();
        carrito.setEstado(EstadoCarrito.VACIO);
        carritoRepo.save(carrito);

        return orden;
    }

    @Override
    public Orden obtenerOrden(Long usuarioId, Long ordenId) {
        Orden o = ordenRepo.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        if (o.getUsuario() == null || !o.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("La orden no pertenece al usuario");
        }
        return o;
    }

    @Override
    public List<Orden> obtenerOrdenes(Long usuarioId) {
        var usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return ordenRepo.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    @Override
    public OrdenResponseDTO convertirAOrdenResponse(Orden o) {
        var items = o.getDetalles().stream().map(i ->
                new ItemOrdenDTO(
                        i.getProducto().getId(),
                        i.getProducto().getNombre(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getSubtotal()
                )
        ).toList();

        String direccionStr = (o.getDireccion() == null)
                ? null
                : o.getDireccion().getCalle() + " " + o.getDireccion().getNumero()
                + ", " + o.getDireccion().getCiudad();

        BigDecimal subtotal = items.stream()
                .map(ItemOrdenDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrdenResponseDTO(
                o.getId(),
                o.getFechaCreacion(),
                o.getEstado(),
                subtotal,
                BigDecimal.ZERO,
                o.getTotal(),
                direccionStr,
                items
        );
    }
}

