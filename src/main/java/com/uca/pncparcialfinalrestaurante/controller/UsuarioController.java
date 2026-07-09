package com.uca.pncparcialfinalrestaurante.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.pncparcialfinalrestaurante.dto.request.UsuarioRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.GeneralResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.UsuarioResponse;
import com.uca.pncparcialfinalrestaurante.entities.Usuario;
import com.uca.pncparcialfinalrestaurante.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.builder()
                        .message("Usuario creado correctamente")
                        .data(toResponse(usuario))
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listar().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Usuarios obtenidos correctamente")
                        .data(usuarios)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> obtener(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Usuario obtenido correctamente")
                        .data(toResponse(usuario))
                        .build());
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombreCompleto(usuario.getNombreCompleto())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .sucursalId(usuario.getSucursal() != null ? usuario.getSucursal().getId() : null)
                .build();
    }
}
