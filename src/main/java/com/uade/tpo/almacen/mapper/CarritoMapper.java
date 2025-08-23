package com.uade.tpo.almacen.mapper;

import com.uade.tpo.almacen.entity.Carrito;
import com.uade.tpo.almacen.entity.ItemCarrito;
import com.uade.tpo.almacen.entity.Producto;
import com.uade.tpo.almacen.entity.dto.CarritoResponse;
import com.uade.tpo.almacen.entity.dto.ItemCarritoDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CarritoMapper {

    public CarritoResponse toResponse(Carrito carrito) {
        if (carrito == null) return null;

        // Mapeo de ítems
        List<ItemCarritoDTO> items = carrito.getItemsCarrito() == null
                ? List.of()
                : carrito.getItemsCarrito()
                         .stream()
                         .filter(Objects::nonNull)
                         .map(this::toItemDto)
                         .collect(Collectors.toList());

        // Cálculo de total (sumatoria de subtotales)
        BigDecimal total = items.stream()
                .map(ItemCarritoDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String estado = carrito.getEstado() != null ? carrito.getEstado().name() : null;

        return new CarritoResponse(
                carrito.getId(),
                estado,
                items,
                total.doubleValue() // tu DTO usa double
        );
    }

    private ItemCarritoDTO toItemDto(ItemCarrito item) {
        if (item == null) {
            return new ItemCarritoDTO(0, null, 0, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        int cantidad = Math.max(0, item.getCantidad());
        BigDecimal precioUnitario = item.getPrecioUnitario() != null ? item.getPrecioUnitario() : BigDecimal.ZERO;
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        Producto prod = item.getProducto();
        int productoId = (prod != null) ? prod.getId() : 0;
        String nombreProducto = (prod != null) ? prod.getNombre() : null;

        return new ItemCarritoDTO(
                productoId,
                nombreProducto,
                cantidad,
                precioUnitario,
                subtotal
        );
    }
}
