package com.uade.tpo.almacen.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class CategoriaResponse {
    private int id;
    private String nombre;
    private Integer parentId;
    private String parentNombre;
    private List<CategoriaResponse> subcategorias;

    public CategoriaResponse(int id, String nombre, Integer parentId, String parentNombre,
                             List<CategoriaResponse> subcategorias) {
        this.id = id;
        this.nombre = nombre;
        this.parentId = parentId;
        this.parentNombre = parentNombre;
        this.subcategorias = subcategorias;
    }
}
