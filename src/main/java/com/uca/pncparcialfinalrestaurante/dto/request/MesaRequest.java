package com.uca.pncparcialfinalrestaurante.dto.request;

import com.uca.pncparcialfinalrestaurante.entities.EstadoMesa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaRequest {

    @NotNull
    @Positive
    private Integer numero;

    @NotNull
    @Positive
    private Integer capacidad;

    @NotNull
    private EstadoMesa estado;

    @NotNull
    private Long sucursalId;
}
