package com.uca.pncparcialfinalrestaurante.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uca.pncparcialfinalrestaurante.exception.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROL = "rol";
    private static final String CLAIM_SUCURSAL_ID = "sucursalId";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey signingKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(AuthenticatedUser user) {
        return buildToken(user, TYPE_ACCESS, accessExpirationMs);
    }

    public String generateRefreshToken(AuthenticatedUser user) {
        return buildToken(user, TYPE_REFRESH, refreshExpirationMs);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("El token es inválido o ha expirado");
        }
    }

    public boolean isAccessToken(Claims claims) {
        return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public boolean isRefreshToken(Claims claims) {
        return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public AuthenticatedUser toAuthenticatedUser(Claims claims) {
        return new AuthenticatedUser(
                claims.get(CLAIM_USER_ID, Long.class),
                claims.getSubject(),
                RoleName.valueOf(claims.get(CLAIM_ROL, String.class)),
                claims.get(CLAIM_SUCURSAL_ID, Long.class));
    }

    private String buildToken(AuthenticatedUser user, String type, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        JwtBuilder builder = Jwts.builder()
                .subject(user.email())
                .claim(CLAIM_USER_ID, user.id())
                .claim(CLAIM_ROL, user.rol().name())
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(expiry);

        if (user.sucursalId() != null) {
            builder.claim(CLAIM_SUCURSAL_ID, user.sucursalId());
        }

        return builder.signWith(signingKey).compact();
    }
}
