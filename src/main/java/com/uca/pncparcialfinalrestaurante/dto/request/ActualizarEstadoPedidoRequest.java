package com.uca.pncparcialfinalrestaurante.dto.request;

import com.uca.pncparcialfinalrestaurante.entities.EstadoPedido;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoPedidoRequest {

    @NotNull
    private EstadoPedido estado;
}
