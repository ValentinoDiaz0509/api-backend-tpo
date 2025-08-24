package com.uade.tpo.almacen.excepciones;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El producto no existe.")
public class ProductoNotFoundException extends Exception {
    
    public ProductoNotFoundException(String message) {
        super(message);
    }
    
}
