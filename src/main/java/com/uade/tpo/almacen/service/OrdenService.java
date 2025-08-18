package com.uade.tpo.almacen.service;

import java.util.List;

import com.uade.tpo.almacen.entity.Orden;
import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.entity.dto.OrdenResponseDTO;

public interface OrdenService {
    Orden finalizarCompra(Usuario usuario, Integer direccionId);
    Orden obtenerOrden(int usuarioId, int ordenId);
    List<Orden> obtenerOrdenes(int usuarioId);
    OrdenResponseDTO convertirAOrdenResponse(Orden orden);
}
