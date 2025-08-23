package com.uade.tpo.almacen.dto;

import java.math.BigDecimal;

public record ItemOrdenDTO(
        int productoId,
        String nombreProducto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
