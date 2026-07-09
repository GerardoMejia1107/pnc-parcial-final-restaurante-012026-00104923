package com.uca.pncparcialfinalrestaurante.dto.response;

import com.uca.pncparcialfinalrestaurante.entities.EstadoMesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaResponse {

    private Long id;

    private Integer numero;

    private Integer capacidad;

    private EstadoMesa estado;

    private Long sucursalId;
}
