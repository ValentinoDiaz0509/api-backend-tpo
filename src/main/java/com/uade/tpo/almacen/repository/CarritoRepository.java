package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.Carrito;
import com.uade.tpo.almacen.entity.EstadoCarrito;
import com.uade.tpo.almacen.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuario(Usuario usuario);

    Optional<Carrito> findByUsuarioId(Long usuarioId);

    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, EstadoCarrito estado);

    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.itemsCarrito " +
           "WHERE c.usuario.id = :usuarioId AND c.estado = :estado")
    Optional<Carrito> findByUsuarioIdAndEstadoConItems(@Param("usuarioId") Long usuarioId,
                                                       @Param("estado") EstadoCarrito estado);

    //  viejos
    List<Carrito> findByEstadoAndFechaActivacionBefore(EstadoCarrito estado, LocalDateTime fechaLimite);
}
