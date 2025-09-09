package com.uade.tpo.almacen.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CarritoResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("estado") String estado,
    @JsonProperty("items") List<ItemCarritoDTO> items,
    @JsonProperty("total") BigDecimal total
) implements Serializable {}
