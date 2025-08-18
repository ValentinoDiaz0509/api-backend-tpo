package com.uade.tpo.almacen.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductoRequest {
    private int categoria_id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private int stock;
    private int stockMinimo;
    private BigDecimal descuento;     // 0..100
    private int ventasTotales;
    private List<String> imagenes;    // ajustá el tipo si usás otra cosa

    public int getCategoria_id() { return categoria_id; }
    public void setCategoria_id(int categoria_id) { this.categoria_id = categoria_id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public int getVentasTotales() { return ventasTotales; }
    public void setVentasTotales(int ventasTotales) { this.ventasTotales = ventasTotales; }

    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
}
