package com.uca.pncparcialfinalrestaurante.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull
    private Long mesaId;

    @NotEmpty
    @Valid
    private List<DetallePedidoRequest> detalles;
}
