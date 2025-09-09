package com.uade.tpo.almacen.entity.dto;

import java.math.BigDecimal;

public record ItemOrdenDTO(
        Long productoId,         
        String nombre,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
