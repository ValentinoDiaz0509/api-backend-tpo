package com.uade.tpo.almacen.controller.dto;

import java.util.List;

public class CategoriaResponse {
    private int id;
    private String nombre;
    private Integer parentId;      
    private String parentNombre;   
    private List<CategoriaResponse> subcategorias;

    public CategoriaResponse() {}

    public CategoriaResponse(int id, String nombre, Integer parentId, String parentNombre, List<CategoriaResponse> subcategorias) {
        this.id = id;
        this.nombre = nombre;
        this.parentId = parentId;
        this.parentNombre = parentNombre;
        this.subcategorias = subcategorias;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Integer getParentId() { return parentId; }
    public String getParentNombre() { return parentNombre; }
    public List<CategoriaResponse> getSubcategorias() { return subcategorias; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public void setParentNombre(String parentNombre) { this.parentNombre = parentNombre; }
    public void setSubcategorias(List<CategoriaResponse> subcategorias) { this.subcategorias = subcategorias; }
}
