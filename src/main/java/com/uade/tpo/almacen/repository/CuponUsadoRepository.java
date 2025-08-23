package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CuponUsadoRepository extends JpaRepository<CuponUsado, Integer> {
    List<CuponUsado> findByCupon(Cupon cupon);
    boolean existsByCuponAndOrden(Cupon cupon, Orden orden);
}
