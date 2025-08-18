package com.uade.tpo.almacen.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.uade.tpo.almacen.entity.Carrito;
import com.uade.tpo.almacen.entity.ItemCarrito;
import com.uade.tpo.almacen.entity.dto.CarritoResponse;
import com.uade.tpo.almacen.entity.dto.ItemCarritoDTO;

@Component
public class CarritoMapper {

    public CarritoResponse toResponse(Carrito carrito) {
        List<ItemCarritoDTO> items = carrito.getItemsCarrito().stream()
                .map(this::toDto)
                .toList();
        double total = items.stream()
                .map(ItemCarritoDTO::subtotal)
                .mapToDouble(d -> d.doubleValue())
                .sum();
        return new CarritoResponse(carrito.getId(), carrito.getEstado().name(), items, total);
    }

    private ItemCarritoDTO toDto(ItemCarrito item) {
        return new ItemCarritoDTO(
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal());
    }
}
