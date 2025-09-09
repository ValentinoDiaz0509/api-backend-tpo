package com.uade.tpo.almacen.entity.dto;

import java.math.BigDecimal;

public record ItemCarritoDTO(
        Long id,            
        Long productoId,
        String nombreProducto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
