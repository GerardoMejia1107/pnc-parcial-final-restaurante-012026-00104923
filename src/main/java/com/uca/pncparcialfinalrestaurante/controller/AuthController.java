package com.uca.pncparcialfinalrestaurante.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.pncparcialfinalrestaurante.dto.request.LoginRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.RegisterRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.AccessTokenResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.AuthResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.GeneralResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.UsuarioResponse;
import com.uca.pncparcialfinalrestaurante.entities.Usuario;
import com.uca.pncparcialfinalrestaurante.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.builder()
                        .message("Usuario registrado correctamente")
                        .data(toUsuarioResponse(usuario))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Inicio de sesión exitoso")
                        .data(authResponse)
                        .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<GeneralResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AccessTokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Token renovado correctamente")
                        .data(response)
                        .build());
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombreCompleto(usuario.getNombreCompleto())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .sucursalId(usuario.getSucursal() != null ? usuario.getSucursal().getId() : null)
                .build();
    }
}
