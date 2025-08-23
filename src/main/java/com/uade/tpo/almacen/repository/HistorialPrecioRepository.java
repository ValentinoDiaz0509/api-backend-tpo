package com.uade.tpo.almacen.repository;

import com.uade.tpo.almacen.entity.HistorialPrecio;
import com.uade.tpo.almacen.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialPrecioRepository extends JpaRepository<HistorialPrecio, Integer> {
    List<HistorialPrecio> findByProductoOrderByFechaCambioDesc(Producto producto);
}
