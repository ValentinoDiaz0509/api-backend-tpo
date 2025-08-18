package com.uade.tpo.almacen.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Entity
@Data
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 20, unique = true, nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TipoCupon tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor; 

    @Column(nullable = false)
    private LocalDate validoHasta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minimoCompra;

    @Column(nullable = false)
    private int usosMaximos;

    public Cupon() {
    }

    public Cupon(String codigo, TipoCupon tipo, BigDecimal valor, LocalDate validoHasta,
                 BigDecimal minimoCompra, int usosMaximos) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.valor = valor;
        this.validoHasta = validoHasta;
        this.minimoCompra = minimoCompra;
        this.usosMaximos = usosMaximos;
    }

    // 🔹 Método helper: validar si un cupón sigue siendo válido
    public boolean esValido(BigDecimal montoCompra) {
        return LocalDate.now().isBefore(validoHasta.plusDays(1)) // todavía no venció
                && montoCompra.compareTo(minimoCompra) >= 0;     // cumple mínimo
    }
}
