package com.uca.pncparcialfinalrestaurante.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Long id;

    private String nombre;

    private BigDecimal precio;

    private boolean disponible;
}
