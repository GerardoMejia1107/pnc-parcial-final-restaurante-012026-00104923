package com.uca.pncparcialfinalrestaurante.security;

public enum RoleName {
    ADMINISTRADOR,
    ENCARGADO_TURNO,
    CLIENTE;

    public String authority() {
        return "ROLE_" + name();
    }
}
