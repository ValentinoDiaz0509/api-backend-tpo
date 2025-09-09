package com.uade.tpo.almacen.controller.dto;

import java.util.List;

public class CategoriaResponse {
    private Long id;
    private String nombre;
    private Long parentId;
    private String parentNombre;
    private List<CategoriaResponse> subcategorias;

    public CategoriaResponse(Long id, String nombre, Long parentId, String parentNombre, List<CategoriaResponse> subcategorias) {
        this.id = id;
        this.nombre = nombre;
        this.parentId = parentId;
        this.parentNombre = parentNombre;
        this.subcategorias = subcategorias;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getParentNombre() { return parentNombre; }
    public void setParentNombre(String parentNombre) { this.parentNombre = parentNombre; }

    public List<CategoriaResponse> getSubcategorias() { return subcategorias; }
    public void setSubcategorias(List<CategoriaResponse> subcategorias) { this.subcategorias = subcategorias; }
}
