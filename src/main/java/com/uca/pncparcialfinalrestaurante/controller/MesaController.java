package com.uca.pncparcialfinalrestaurante.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.pncparcialfinalrestaurante.dto.request.MesaRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.GeneralResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.MesaResponse;
import com.uca.pncparcialfinalrestaurante.entities.Mesa;
import com.uca.pncparcialfinalrestaurante.security.CustomUserDetails;
import com.uca.pncparcialfinalrestaurante.service.MesaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<GeneralResponse> crear(
            @Valid @RequestBody MesaRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Mesa mesa = mesaService.crear(request, principal.toAuthenticatedUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.builder()
                        .message("Mesa creada correctamente")
                        .data(toResponse(mesa))
                        .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar(@AuthenticationPrincipal CustomUserDetails principal) {
        List<MesaResponse> mesas = mesaService.listar(principal.toAuthenticatedUser()).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Mesas obtenidas correctamente")
                        .data(mesas)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtener(@PathVariable Long id) {
        Mesa mesa = mesaService.obtenerPorId(id);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Mesa obtenida correctamente")
                        .data(toResponse(mesa))
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<GeneralResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MesaRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Mesa mesa = mesaService.actualizar(id, request, principal.toAuthenticatedUser());
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Mesa actualizada correctamente")
                        .data(toResponse(mesa))
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<GeneralResponse> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        mesaService.eliminar(id, principal.toAuthenticatedUser());
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Mesa eliminada correctamente")
                        .build());
    }

    private MesaResponse toResponse(Mesa mesa) {
        return MesaResponse.builder()
                .id(mesa.getId())
                .numero(mesa.getNumero())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado())
                .sucursalId(mesa.getSucursal().getId())
                .build();
    }
}
