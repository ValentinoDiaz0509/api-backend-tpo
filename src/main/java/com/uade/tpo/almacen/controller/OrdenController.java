package com.uade.tpo.almacen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.almacen.entity.Orden;
import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.dto.OrdenResponseDTO;
import com.uade.tpo.almacen.excepciones.NoEncontradoException;
import com.uade.tpo.almacen.service.OrdenService;
import com.uade.tpo.almacen.service.UsuarioService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("ordenes")
public class OrdenController {
    @Autowired
    private OrdenService ordenService;

    @Autowired
    private UsuarioService usuarioService;

    // DTO para finalizar compra
    public static class FinalizarCompraRequest {
        public Integer direccionId; // null para retiro en tienda
    }

    // POST para finalizar compra
    @PostMapping
    public ResponseEntity<OrdenResponseDTO> finalizarCompra(
            Principal principal,
            @RequestBody FinalizarCompraRequest request) {
        String username = principal.getName();
        Usuario usuario = usuarioService.getUsuarioByUsername(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));
        Orden orden = ordenService.finalizarCompra(usuario, request.direccionId);
        OrdenResponseDTO dto = ordenService.convertirAOrdenResponse(orden);
        return ResponseEntity.ok(dto);
    }

    // GET una orden por id
    @GetMapping("/{ordenId}/usuarios/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerOrden(@PathVariable int id, @PathVariable int ordenId) {
        Orden orden = ordenService.obtenerOrden(id, ordenId);
        OrdenResponseDTO dto = ordenService.convertirAOrdenResponse(orden);
        return ResponseEntity.ok(dto);
    }

    // GET historial de ordenes por usuario
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerOrdenes(@PathVariable int id) {
        List<Orden> ordenes = ordenService.obtenerOrdenes(id);
        List<OrdenResponseDTO> dtos = ordenes.stream()
                .map(ordenService::convertirAOrdenResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
