package com.uca.pncparcialfinalrestaurante.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    private String telefono;
}
