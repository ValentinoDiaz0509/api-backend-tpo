package com.uade.tpo.almacen.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrdenResponseDTO(
        int ordenId,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
        LocalDateTime fechaCreacion,
        String estado,
        BigDecimal subtotal,
        BigDecimal descuentoTotal,
        BigDecimal total,
        String direccion,               
        List<ItemOrdenDTO> items
) {}
