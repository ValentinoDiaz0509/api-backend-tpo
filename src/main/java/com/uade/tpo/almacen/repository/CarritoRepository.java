package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.*;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findByUsuario(Usuario usuario);
    Optional<Carrito> findByUsuarioIdAndEstado(int usuarioId, EstadoCarrito estado);

    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.itemsCarrito " +
           "WHERE c.usuario.id = :usuarioId AND c.estado = :estado")
    Optional<Carrito> findByUsuarioIdAndEstadoConItems(@Param("usuarioId") int usuarioId,
                                                       @Param("estado") EstadoCarrito estado);

    List<Carrito> findByEstadoAndFechaActivacionBefore(EstadoCarrito estado, LocalDateTime fechaLimite);
}
