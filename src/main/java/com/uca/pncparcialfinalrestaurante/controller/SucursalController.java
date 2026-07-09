package com.uca.pncparcialfinalrestaurante.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.pncparcialfinalrestaurante.dto.request.SucursalRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.GeneralResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.SucursalResponse;
import com.uca.pncparcialfinalrestaurante.entities.Sucursal;
import com.uca.pncparcialfinalrestaurante.service.SucursalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> crear(@Valid @RequestBody SucursalRequest request) {
        Sucursal sucursal = sucursalService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.builder()
                        .message("Sucursal creada correctamente")
                        .data(toResponse(sucursal))
                        .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar() {
        List<SucursalResponse> sucursales = sucursalService.listar().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Sucursales obtenidas correctamente")
                        .data(sucursales)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtener(@PathVariable Long id) {
        Sucursal sucursal = sucursalService.obtenerPorId(id);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Sucursal obtenida correctamente")
                        .data(toResponse(sucursal))
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> actualizar(@PathVariable Long id, @Valid @RequestBody SucursalRequest request) {
        Sucursal sucursal = sucursalService.actualizar(id, request);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Sucursal actualizada correctamente")
                        .data(toResponse(sucursal))
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Sucursal eliminada correctamente")
                        .build());
    }

    private SucursalResponse toResponse(Sucursal sucursal) {
        return SucursalResponse.builder()
                .id(sucursal.getId())
                .nombre(sucursal.getNombre())
                .direccion(sucursal.getDireccion())
                .telefono(sucursal.getTelefono())
                .build();
    }
}
