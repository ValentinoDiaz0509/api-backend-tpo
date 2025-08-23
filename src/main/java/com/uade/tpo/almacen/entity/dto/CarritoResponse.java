package com.uade.tpo.almacen.entity.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CarritoResponse(
    @JsonProperty("id") int id,
    @JsonProperty("estado") String estado,
    @JsonProperty("items") List<ItemCarritoDTO> items,
    @JsonProperty("total") double total
) implements Serializable {}
