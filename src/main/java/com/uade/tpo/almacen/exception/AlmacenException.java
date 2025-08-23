package com.uade.tpo.almacen.exception;

public class AlmacenException extends RuntimeException {
    public AlmacenException(String message) { super(message); }
    public AlmacenException(String message, Throwable cause) { super(message, cause); }
}
