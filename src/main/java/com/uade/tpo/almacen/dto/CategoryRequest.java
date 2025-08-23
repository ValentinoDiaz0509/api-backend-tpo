package com.uade.tpo.almacen.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private String nombre;    
    private Integer parentId;  
}
