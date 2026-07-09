package com.uca.pncparcialfinalrestaurante.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uca.pncparcialfinalrestaurante.dto.request.LoginRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.RegisterRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.AccessTokenResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.AuthResponse;
import com.uca.pncparcialfinalrestaurante.entities.Usuario;
import com.uca.pncparcialfinalrestaurante.exception.EmailAlreadyExistsException;
import com.uca.pncparcialfinalrestaurante.exception.InvalidCredentialsException;
import com.uca.pncparcialfinalrestaurante.exception.InvalidTokenException;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.AuthenticatedUser;
import com.uca.pncparcialfinalrestaurante.security.JwtUtil;
import com.uca.pncparcialfinalrestaurante.security.RoleName;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Usuario registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con el correo " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(RoleName.CLIENTE)
                .build();

        return usuarioRepository.save(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        AuthenticatedUser authenticatedUser = toAuthenticatedUser(usuario);

        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(authenticatedUser))
                .refreshToken(jwtUtil.generateRefreshToken(authenticatedUser))
                .build();
    }

    public AccessTokenResponse refresh(RefreshTokenRequest request) {
        Claims claims = jwtUtil.parseClaims(request.getRefreshToken());

        if (!jwtUtil.isRefreshToken(claims)) {
            throw new InvalidTokenException("El token proporcionado no es un refresh token");
        }

        Usuario usuario = usuarioRepository.findByEmail(claims.getSubject())
                .orElseThrow(() -> new InvalidTokenException("El usuario del token ya no existe"));

        String accessToken = jwtUtil.generateAccessToken(toAuthenticatedUser(usuario));

        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    private AuthenticatedUser toAuthenticatedUser(Usuario usuario) {
        Long sucursalId = usuario.getSucursal() != null ? usuario.getSucursal().getId() : null;
        return new AuthenticatedUser(usuario.getId(), usuario.getEmail(), usuario.getRol(), sucursalId);
    }
}
