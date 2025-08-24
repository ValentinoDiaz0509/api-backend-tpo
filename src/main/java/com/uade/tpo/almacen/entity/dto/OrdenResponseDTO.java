package com.uade.tpo.almacen.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponseDTO(
        Long id,
        LocalDateTime fecha,      
        String estado,
        BigDecimal subtotal,
        BigDecimal descuentoTotal,
        BigDecimal total,
        String direccion,
        List<ItemOrdenDTO> items
) {}
