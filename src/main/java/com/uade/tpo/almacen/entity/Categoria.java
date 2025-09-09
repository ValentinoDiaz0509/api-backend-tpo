package com.uade.tpo.almacen.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


    @NotBlank
    @Size(max = 50)
    @Column(length = 50, nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @JsonBackReference
    private Categoria parentCategoria;

    @OneToMany(mappedBy = "parentCategoria", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Categoria> subcategorias = new ArrayList<>();

    // Productos asociados
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();

    public void addSubcategoria(Categoria sub) {
        if (sub == null) return;
        sub.setParentCategoria(this);
        this.subcategorias.add(sub);
    }

    public void addProducto(Producto p) {
        if (p == null) return;
        p.setCategoria(this);
        this.productos.add(p);
    }
}
