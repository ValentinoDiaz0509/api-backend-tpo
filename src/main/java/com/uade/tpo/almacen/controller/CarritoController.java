package com.uade.tpo.almacen.controller;

import com.uade.tpo.almacen.entity.Carrito;
import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.entity.dto.CarritoResponse;
import com.uade.tpo.almacen.excepciones.NoEncontradoException;
import com.uade.tpo.almacen.mapper.CarritoMapper;
import com.uade.tpo.almacen.service.api.CarritoService;
import com.uade.tpo.almacen.service.impl.UsuarioService;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/carritos")
@Validated
public class CarritoController {

    private final CarritoService carritoService;   
    private final UsuarioService usuarioService;
    private final CarritoMapper carritoMapper;

    public CarritoController(CarritoService carritoService,
                             UsuarioService usuarioService,
                             CarritoMapper carritoMapper) {
        this.carritoService = carritoService;      
        this.usuarioService = usuarioService;
        this.carritoMapper = carritoMapper;
    }

    @PostMapping
    public ResponseEntity<CarritoResponse> crearCarrito(Principal principal) {
        Usuario usuario = getUsuarioDesdePrincipal(principal);
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario.getId()); // ✅ usa tu firma real
        return ResponseEntity
                .created(URI.create("/carritos/" + carrito.getId()))
                .body(carritoMapper.toResponse(carrito));
    }

    @GetMapping
    public ResponseEntity<CarritoResponse> obtenerCarrito(Principal principal) {
        Usuario usuario = getUsuarioDesdePrincipal(principal);
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuario.getId()); // ✅
        return ResponseEntity.ok(carritoMapper.toResponse(carrito));
    }

    @PatchMapping("/productos/{productoId}")
    public ResponseEntity<CarritoResponse> agregarProducto(
            Principal principal,
            @PathVariable int productoId,
            @RequestParam(defaultValue = "1") @Min(1) int cantidad) {
        Usuario usuario = getUsuarioDesdePrincipal(principal);
        Carrito carritoActualizado = carritoService.agregarItem(usuario.getId(), productoId, cantidad); // ✅
        return ResponseEntity.ok(carritoMapper.toResponse(carritoActualizado));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> quitarItem(
            Principal principal,
            @PathVariable int itemId) {
        Usuario usuario = getUsuarioDesdePrincipal(principal);
        Carrito carritoActualizado = carritoService.quitarItem(usuario.getId(), itemId); // ✅
        return ResponseEntity.ok(carritoMapper.toResponse(carritoActualizado));
    }

    @DeleteMapping
    public ResponseEntity<CarritoResponse> vaciarCarrito(Principal principal) {
        Usuario usuario = getUsuarioDesdePrincipal(principal);
        carritoService.vaciarCarrito(usuario.getId()); // ✅ void
        Carrito carritoActual = carritoService.obtenerOCrearCarrito(usuario.getId()); // ✅ leo para responder
        return ResponseEntity.ok(carritoMapper.toResponse(carritoActual));
    }


    private Usuario getUsuarioDesdePrincipal(Principal principal) {
        if (principal == null) throw new NoEncontradoException("Sesión no válida o no autenticada");
        String username = principal.getName();
        return usuarioService.getUsuarioByUsername(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<String> manejarNoEncontrado(NoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
