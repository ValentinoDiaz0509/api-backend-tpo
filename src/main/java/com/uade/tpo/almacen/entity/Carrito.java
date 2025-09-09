package com.uade.tpo.almacen.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = true)
    private LocalDateTime fechaActivacion;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCarrito estado = EstadoCarrito.VACIO; // por defecto vacío

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ItemCarrito> itemsCarrito = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public void agregarItem(ItemCarrito item) {
        itemsCarrito.add(item);
        item.setCarrito(this);
        if (estado == EstadoCarrito.VACIO) {
            estado = EstadoCarrito.ACTIVO;
            fechaActivacion = LocalDateTime.now();
        }
    }

    public void vaciar() {
        itemsCarrito.clear();
        estado = EstadoCarrito.VACIO;
    }
}
