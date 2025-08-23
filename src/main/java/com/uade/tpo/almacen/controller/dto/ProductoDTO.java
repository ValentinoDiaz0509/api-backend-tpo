package com.uade.tpo.almacen.controller.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uade.tpo.almacen.entity.Imagen;
import com.uade.tpo.almacen.entity.Producto;

public record ProductoDTO(
    @JsonProperty("id") int id,
    @JsonProperty("nombre") String nombre,
    @JsonProperty("descripcion") String descripcion,
    @JsonProperty("imagenes") List<String> imagenes,
    @JsonProperty("precio") BigDecimal precio,
    @JsonProperty("marca") String marca,
    @JsonProperty("categoria") String categoria,
    @JsonProperty("stock") int stock,
    @JsonProperty("unidad_medida") String unidadMedida,
    @JsonProperty("descuento") BigDecimal descuento
) implements Serializable {

    public ProductoDTO(Producto producto) {
        this(
            producto.getId(),
            producto.getNombre(),
            producto.getDescripcion(),
            extractImagenes(producto),
            producto.getPrecio(),
            producto.getMarca(),
            producto.getCategoria().getNombre(),
            producto.getStock(),
            producto.getUnidadMedida(),
            producto.getDescuento()
        );
    }

    private static List<String> extractImagenes(Producto producto) {
        List<String> imagenes = new ArrayList<>();
        for (Imagen imagen : producto.getImagenes()) {
            imagenes.add(imagen.getImagen());
        }
        return imagenes;
    }
}
