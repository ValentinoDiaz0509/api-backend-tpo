package com.uade.tpo.almacen.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class ManejoErrores {

    private ResponseEntity<Object> body(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", mensaje
                )
        );
    }

    // -------- 400 Bad Request
    @ExceptionHandler({
            ParametroFueraDeRangoException.class,
            EstadoInvalidoException.class,
            DireccionInvalidaException.class
    })
    public ResponseEntity<Object> badRequest(RuntimeException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // -------- 401 Unauthorized
    @ExceptionHandler({
            UsuarioNoAutorizadoException.class,
            TokenInvalidoException.class
    })
    public ResponseEntity<Object> unauthorized(RuntimeException ex) {
        return body(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // -------- 403 Forbidden
    @ExceptionHandler({
            OperacionNoPermitidaException.class
    })
    public ResponseEntity<Object> forbidden(RuntimeException ex) {
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // -------- 404 Not Found
    @ExceptionHandler({
            NoEncontradoException.class,
            ProductoNotFoundException.class,
            CategoriaNoEncontradaException.class,
            UsuarioNoEncontradoException.class
    })
    public ResponseEntity<Object> notFound(RuntimeException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // -------- 409 Conflict
    @ExceptionHandler({
            DatoDuplicadoException.class,
            ProductoDuplicateException.class,
            StockInsuficienteException.class
    })
    public ResponseEntity<Object> conflict(RuntimeException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    // -------- 422 Unprocessable Entity (negocio)
    @ExceptionHandler({
            CarritoVacioException.class,
            PagoFallidoException.class
    })
    public ResponseEntity<Object> unprocessable(RuntimeException ex) {
        return body(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // -------- 500 (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> serverError(Exception ex) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + ex.getMessage());
    }
}
