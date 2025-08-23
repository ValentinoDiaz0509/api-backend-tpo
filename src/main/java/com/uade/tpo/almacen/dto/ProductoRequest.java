package com.uade.tpo.almacen.dto;

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

    // Usar Integer para poder chequear null
    private Integer categoria_id;

    // NUEVO: imágenes (urls/base64/lo que uses)
    // La inicializo para evitar NPE y que getImagenes() exista siempre.
    private List<String> imagenes = new ArrayList<>();
}
