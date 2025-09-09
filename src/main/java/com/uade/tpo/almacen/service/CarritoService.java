package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Carrito;

public interface CarritoService {
    Carrito obtenerOCrearCarrito(Long usuarioId);
    Carrito agregarItem(Long usuarioId, Long productoId, int cantidad);
    Carrito quitarItem(Long usuarioId, Long itemId);
    void vaciarCarrito(Long usuarioId);
}
