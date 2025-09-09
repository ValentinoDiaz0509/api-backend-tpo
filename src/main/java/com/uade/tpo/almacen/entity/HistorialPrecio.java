package com.uade.tpo.almacen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_precio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal precio;

    private LocalDateTime fechaCambio;
}
