package com.uade.tpo.almacen.entity.dto;

import java.math.BigDecimal;

public record ItemOrdenDTO(
        Integer productoId,     
        String nombre,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
