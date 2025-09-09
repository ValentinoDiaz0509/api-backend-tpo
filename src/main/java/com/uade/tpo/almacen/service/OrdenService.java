package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Orden;
import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.entity.dto.OrdenResponseDTO;

import java.util.List;

public interface OrdenService {
    Orden finalizarCompra(Usuario usuario, Long direccionId);
    Orden obtenerOrden(Long usuarioId, Long ordenId);   
    List<Orden> obtenerOrdenes(Long usuarioId);        

    OrdenResponseDTO convertirAOrdenResponse(Orden orden);
}
