package com.uca.pncparcialfinalrestaurante.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.uca.pncparcialfinalrestaurante.entities.EstadoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Long id;

    private Long clienteId;

    private String clienteNombre;

    private Long mesaId;

    private Integer mesaNumero;

    private Long sucursalId;

    private EstadoPedido estado;

    private LocalDateTime fechaCreacion;

    private List<DetallePedidoResponse> detalles;

    private BigDecimal total;
}
