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

import com.uca.pncparcialfinalrestaurante.dto.request.ProductoRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.GeneralResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.ProductoResponse;
import com.uca.pncparcialfinalrestaurante.entities.Producto;
import com.uca.pncparcialfinalrestaurante.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> crear(@Valid @RequestBody ProductoRequest request) {
        Producto producto = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.builder()
                        .message("Producto creado correctamente")
                        .data(toResponse(producto))
                        .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar() {
        List<ProductoResponse> productos = productoService.listar().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Productos obtenidos correctamente")
                        .data(productos)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtener(@PathVariable Long id) {
        Producto producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Producto obtenido correctamente")
                        .data(toResponse(producto))
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        Producto producto = productoService.actualizar(id, request);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Producto actualizado correctamente")
                        .data(toResponse(producto))
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Producto eliminado correctamente")
                        .build());
    }

    private ProductoResponse toResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .disponible(producto.isDisponible())
                .build();
    }
}
