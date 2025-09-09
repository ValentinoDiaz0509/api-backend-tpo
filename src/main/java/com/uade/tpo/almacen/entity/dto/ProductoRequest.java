package com.uade.tpo.almacen.entity.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private String marca;
    private BigDecimal precio;

    private String unidadMedida;
    private BigDecimal descuento;
    private Integer stock;
    private Integer stockMinimo;
    private Integer ventasTotales;
    private String estado;

    private Long categoria_id; 

    private List<String> imagenes = new ArrayList<>();
}
