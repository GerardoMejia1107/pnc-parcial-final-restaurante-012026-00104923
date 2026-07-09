package com.uca.pncparcialfinalrestaurante.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.pncparcialfinalrestaurante.dto.request.ActualizarEstadoPedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.PedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.DetallePedidoResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.GeneralResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.PedidoResponse;
import com.uca.pncparcialfinalrestaurante.entities.DetallePedido;
import com.uca.pncparcialfinalrestaurante.entities.Pedido;
import com.uca.pncparcialfinalrestaurante.security.CustomUserDetails;
import com.uca.pncparcialfinalrestaurante.service.PedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<GeneralResponse> crear(
            @Valid @RequestBody PedidoRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Pedido pedido = pedidoService.crear(request, principal.toAuthenticatedUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GeneralResponse.builder()
                        .message("Pedido creado correctamente")
                        .data(toResponse(pedido))
                        .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar(@AuthenticationPrincipal CustomUserDetails principal) {
        List<PedidoResponse> pedidos = pedidoService.listar(principal.toAuthenticatedUser()).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Pedidos obtenidos correctamente")
                        .data(pedidos)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Pedido pedido = pedidoService.obtenerPorId(id, principal.toAuthenticatedUser());
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Pedido obtenido correctamente")
                        .data(toResponse(pedido))
                        .build());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<GeneralResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoPedidoRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Pedido pedido = pedidoService.actualizarEstado(id, request, principal.toAuthenticatedUser());
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Estado del pedido actualizado correctamente")
                        .data(toResponse(pedido))
                        .build());
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<GeneralResponse> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Pedido pedido = pedidoService.cancelar(id, principal.toAuthenticatedUser());
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .message("Pedido cancelado correctamente")
                        .data(toResponse(pedido))
                        .build());
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<DetallePedidoResponse> detalles = pedido.getDetalles().stream()
                .map(this::toDetalleResponse)
                .toList();

        BigDecimal total = detalles.stream()
                .map(DetallePedidoResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .clienteNombre(pedido.getCliente().getNombreCompleto())
                .mesaId(pedido.getMesa().getId())
                .mesaNumero(pedido.getMesa().getNumero())
                .sucursalId(pedido.getMesa().getSucursal().getId())
                .estado(pedido.getEstado())
                .fechaCreacion(pedido.getFechaCreacion())
                .detalles(detalles)
                .total(total)
                .build();
    }

    private DetallePedidoResponse toDetalleResponse(DetallePedido detalle) {
        BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));

        return DetallePedidoResponse.builder()
                .id(detalle.getId())
                .productoId(detalle.getProducto().getId())
                .productoNombre(detalle.getProducto().getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(subtotal)
                .build();
    }
}
