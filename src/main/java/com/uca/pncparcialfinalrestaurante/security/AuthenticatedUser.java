package com.uca.pncparcialfinalrestaurante.security;

/**
 * Minimal view of the acting user that the service layer needs for attribute-based authorization
 * (comparing sucursalId, checking ownership) without depending on the full Spring Security wiring.
 * The controller layer is responsible for resolving this from the security context.
 */
public record AuthenticatedUser(Long id, String email, RoleName rol, Long sucursalId) {
}
