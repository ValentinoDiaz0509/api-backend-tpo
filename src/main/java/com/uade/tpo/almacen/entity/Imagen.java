package com.uade.tpo.almacen.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imagen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;
}
