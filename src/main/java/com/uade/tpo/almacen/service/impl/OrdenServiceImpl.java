package com.uade.tpo.almacen.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.almacen.entity.*;
import com.uade.tpo.almacen.entity.dto.ItemOrdenDTO;
import com.uade.tpo.almacen.entity.dto.OrdenResponseDTO;
import com.uade.tpo.almacen.repository.*;
import com.uade.tpo.almacen.service.OrdenService;

@Service
@Transactional
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final CarritoRepository carritoRepository;
    private final DireccionRepository direccionRepository;

    public OrdenServiceImpl(OrdenRepository ordenRepository,
                            CarritoRepository carritoRepository,
                            DireccionRepository direccionRepository) {
        this.ordenRepository = ordenRepository;
        this.carritoRepository = carritoRepository;
        this.direccionRepository = direccionRepository;
    }

    @Override
    public Orden finalizarCompra(Usuario usuario, Integer direccionId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuario.getId()).orElseThrow();
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE);
        if (direccionId != null) {
            direccionRepository.findById(direccionId).ifPresent(orden::setDireccion);
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCarrito item : carrito.getItemsCarrito()) {
            DetalleOrden detalle = DetalleOrden.builder()
                    .orden(orden)
                    .producto(item.getProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(item.getSubtotal())
                    .build();
            orden.getDetalles().add(detalle);
            subtotal = subtotal.add(item.getSubtotal());
        }
        orden.setSubtotal(subtotal);
        orden.setDescuentoTotal(BigDecimal.ZERO);
        orden.setTotal(subtotal);
        Orden guardada = ordenRepository.save(orden);
        carrito.vaciar();
        carritoRepository.save(carrito);
        return guardada;
    }

    @Override
    public Orden obtenerOrden(int usuarioId, int ordenId) {
        return ordenRepository.findByIdAndUsuarioId(ordenId, usuarioId).orElseThrow();
    }

    @Override
    public List<Orden> obtenerOrdenes(int usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public OrdenResponseDTO convertirAOrdenResponse(Orden orden) {
        List<ItemOrdenDTO> items = orden.getDetalles().stream()
                .map(d -> new ItemOrdenDTO(
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .collect(Collectors.toList());
        String direccion = orden.getDireccion() != null
                ? orden.getDireccion().getCalle() + " " + orden.getDireccion().getNumero()
                : null;
        return new OrdenResponseDTO(
                orden.getId(),
                orden.getFechaCreacion(),
                orden.getEstado().name(),
                orden.getSubtotal(),
                orden.getDescuentoTotal(),
                orden.getTotal(),
                direccion,
                items);
    }
}
