package com.uade.tpo.almacen.entity.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor          // genera constructor vacío
@AllArgsConstructor        // genera constructor con todos los campos
public class CarritoResponse {
    private int id;
    private String estado;
    private List<ItemCarritoDTO> items;
    private double total;
}
