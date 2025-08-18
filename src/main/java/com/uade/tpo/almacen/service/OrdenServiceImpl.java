package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Carrito;
import com.uade.tpo.almacen.entity.DetalleOrden;
import com.uade.tpo.almacen.entity.Direccion;
import com.uade.tpo.almacen.entity.EstadoCarrito;
import com.uade.tpo.almacen.entity.EstadoOrden;
import com.uade.tpo.almacen.entity.ItemCarrito;
import com.uade.tpo.almacen.entity.Orden;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.entity.dto.ItemOrdenDTO;
import com.uade.tpo.almacen.entity.dto.OrdenResponseDTO;
import com.uade.tpo.almacen.repository.CarritoRepository;
import com.uade.tpo.almacen.repository.DetalleOrdenRepository;
import com.uade.tpo.almacen.repository.DireccionRepository;
import com.uade.tpo.almacen.repository.OrdenRepository;
import com.uade.tpo.almacen.repository.ProductoRepository;
import com.uade.tpo.almacen.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public Orden finalizarCompra(Usuario usuario, Integer direccionId) {
        // 1) Carrito ACTIVO con ítems
        Carrito carrito = carritoRepo
                .findByUsuarioIdAndEstadoConItems(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new IllegalArgumentException("No hay carrito activo para el usuario"));

        if (carrito.getItemsCarrito().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        // 2) Crear orden base (total=0 para cumplir NOT NULL)
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE);
        orden.setDescuentoTotal(BigDecimal.ZERO);
        orden.setTotal(BigDecimal.ZERO); // importante antes del primer save

        if (direccionId != null) {
            Direccion direccion = direccionRepo.findById(direccionId)
                    .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada"));
            orden.setDireccionEnvio(direccion);
        }
        orden = ordenRepo.save(orden);

        // 3) Generar detalles, controlar stock y calcular subtotal
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemCarrito ic : carrito.getItemsCarrito()) {
            Producto p = productoRepo.findById(ic.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (p.getStock() < ic.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para " + p.getNombre());
            }

            // precio unitario con descuento por producto (si aplica)
            BigDecimal precioUnit = p.getPrecio();
            if (p.getDescuento() != null && p.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal porc = p.getDescuento().divide(new BigDecimal("100"));
                precioUnit = p.getPrecio().multiply(BigDecimal.ONE.subtract(porc)).setScale(2);
            }

            BigDecimal itemSubtotal = precioUnit
                    .multiply(BigDecimal.valueOf(ic.getCantidad()))
                    .setScale(2);
            subtotal = subtotal.add(itemSubtotal);

            // Detalle de la orden (requiere @Builder en la entidad DetalleOrden)
            DetalleOrden det = DetalleOrden.builder()
                    .cantidad(ic.getCantidad())
                    .precioUnitario(precioUnit)
                    .subtotal(itemSubtotal)
                    .orden(orden)
                    .producto(p)
                    .build();
            detalleRepo.save(det);

            // actualizar stock y ventas
            p.setStock(p.getStock() - ic.getCantidad());
            p.setVentasTotales(p.getVentasTotales() + ic.getCantidad());
            productoRepo.save(p);
        }

        // 4) Total y persistir
        orden.setDescuentoTotal(BigDecimal.ZERO); // si luego agregás cupones, ajustá acá
        orden.setTotal(subtotal.setScale(2));
        orden = ordenRepo.save(orden);

        // 5) Vaciar carrito
        carrito.getItemsCarrito().clear();
        carrito.setEstado(EstadoCarrito.VACIO);
        carritoRepo.save(carrito);

        return orden;
    }

    @Override
    public Orden obtenerOrden(int usuarioId, int ordenId) {
        Orden o = ordenRepo.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        if (o.getUsuario() == null || o.getUsuario().getId() != usuarioId) {
            throw new IllegalArgumentException("La orden no pertenece al usuario");
        }
        return o;
    }

    @Override
    public List<Orden> obtenerOrdenes(int usuarioId) {
        var usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return ordenRepo.findByUsuarioOrderByFechaDesc(usuario);
    }

    @Override
    public OrdenResponseDTO convertirAOrdenResponse(Orden o) {
        var items = o.getItemsOrden().stream().map(i ->
                new ItemOrdenDTO(
                        i.getProducto().getId(),
                        i.getProducto().getNombre(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getSubtotal()
                )
        ).toList();

        String direccion = (o.getDireccionEnvio() == null)
                ? null
                : o.getDireccionEnvio().getCalle() + " " + o.getDireccionEnvio().getNumero()
                  + ", " + o.getDireccionEnvio().getCiudad();

        BigDecimal subtotal = items.stream()
                .map(ItemOrdenDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrdenResponseDTO(
                o.getId(),
                o.getFecha(),
                o.getEstado().name(),      // Enum -> String para el DTO
                subtotal,
                o.getDescuentoTotal(),
                o.getTotal(),
                direccion,
                items
        );
    }
}
