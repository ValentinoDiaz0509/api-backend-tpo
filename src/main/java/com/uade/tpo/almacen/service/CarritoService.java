package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Carrito;

public interface CarritoService {
    Carrito obtenerOCrearCarrito(int usuarioId);
    Carrito agregarItem(int usuarioId, int productoId, int cantidad);
    Carrito quitarItem(int usuarioId, int itemId);
    void vaciarCarrito(int usuarioId);
}
