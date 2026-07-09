package com.uca.pncparcialfinalrestaurante.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown specifically when an Encargado de turno tries to act on a mesa/pedido that belongs to a
 * different sucursal than the one they are assigned to. This is the mechanism for the assignment's
 * non-trivial business rule (Option B: attribute-based authorization).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenSucursalException extends RuntimeException {

    public ForbiddenSucursalException(String message) {
        super(message);
    }
}
