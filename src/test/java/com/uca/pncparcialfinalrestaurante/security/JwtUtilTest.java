package com.uca.pncparcialfinalrestaurante.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.uca.pncparcialfinalrestaurante.exception.InvalidTokenException;

import io.jsonwebtoken.Claims;

class JwtUtilTest {

    private static final String SECRET = "unit-test-secret-key-with-enough-length-for-hs256-0123456789";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 900_000L, 604_800_000L);

    @Test
    void generaYValidaUnAccessToken() {
        AuthenticatedUser user = new AuthenticatedUser(1L, "admin@test.com", RoleName.ADMINISTRADOR, null);

        String token = jwtUtil.generateAccessToken(user);
        Claims claims = jwtUtil.parseClaims(token);

        assertThat(jwtUtil.isAccessToken(claims)).isTrue();
        assertThat(jwtUtil.isRefreshToken(claims)).isFalse();

        AuthenticatedUser parsed = jwtUtil.toAuthenticatedUser(claims);
        assertThat(parsed.id()).isEqualTo(1L);
        assertThat(parsed.email()).isEqualTo("admin@test.com");
        assertThat(parsed.rol()).isEqualTo(RoleName.ADMINISTRADOR);
        assertThat(parsed.sucursalId()).isNull();
    }

    @Test
    void generaYValidaUnRefreshToken() {
        AuthenticatedUser user = new AuthenticatedUser(2L, "encargado@test.com", RoleName.ENCARGADO_TURNO, 5L);

        String token = jwtUtil.generateRefreshToken(user);
        Claims claims = jwtUtil.parseClaims(token);

        assertThat(jwtUtil.isRefreshToken(claims)).isTrue();
        assertThat(jwtUtil.isAccessToken(claims)).isFalse();

        AuthenticatedUser parsed = jwtUtil.toAuthenticatedUser(claims);
        assertThat(parsed.sucursalId()).isEqualTo(5L);
    }

    @Test
    void unTokenExpiradoLanzaInvalidTokenException() throws InterruptedException {
        JwtUtil jwtUtilExpiracionCorta = new JwtUtil(SECRET, 1L, 1L);
        AuthenticatedUser user = new AuthenticatedUser(3L, "cliente@test.com", RoleName.CLIENTE, null);

        String token = jwtUtilExpiracionCorta.generateAccessToken(user);
        Thread.sleep(20);

        assertThatThrownBy(() -> jwtUtilExpiracionCorta.parseClaims(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void unTokenManipuladoLanzaInvalidTokenException() {
        AuthenticatedUser user = new AuthenticatedUser(4L, "cliente2@test.com", RoleName.CLIENTE, null);
        String token = jwtUtil.generateAccessToken(user);
        String tokenManipulado = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> jwtUtil.parseClaims(tokenManipulado))
                .isInstanceOf(InvalidTokenException.class);
    }
}
