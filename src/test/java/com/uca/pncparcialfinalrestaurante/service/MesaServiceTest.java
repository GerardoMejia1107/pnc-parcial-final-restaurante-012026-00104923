package com.uca.pncparcialfinalrestaurante.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uca.pncparcialfinalrestaurante.dto.request.MesaRequest;
import com.uca.pncparcialfinalrestaurante.entities.EstadoMesa;
import com.uca.pncparcialfinalrestaurante.entities.Mesa;
import com.uca.pncparcialfinalrestaurante.entities.Sucursal;
import com.uca.pncparcialfinalrestaurante.exception.ForbiddenSucursalException;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.repository.SucursalRepository;
import com.uca.pncparcialfinalrestaurante.security.AuthenticatedUser;
import com.uca.pncparcialfinalrestaurante.security.RoleName;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private MesaService mesaService;

    private Sucursal sucursalUno;
    private Sucursal sucursalDos;

    @BeforeEach
    void setUp() {
        sucursalUno = Sucursal.builder().id(1L).nombre("Sucursal Centro").build();
        sucursalDos = Sucursal.builder().id(2L).nombre("Sucursal Norte").build();
    }

    @Test
    void unEncargadoNoPuedeCrearMesasEnOtraSucursal() {
        MesaRequest request = MesaRequest.builder()
                .numero(1)
                .capacidad(4)
                .estado(EstadoMesa.LIBRE)
                .sucursalId(2L)
                .build();

        AuthenticatedUser encargadoDeLaSucursalUno =
                new AuthenticatedUser(10L, "encargado@test.com", RoleName.ENCARGADO_TURNO, 1L);

        when(sucursalRepository.findById(2L)).thenReturn(Optional.of(sucursalDos));

        assertThatThrownBy(() -> mesaService.crear(request, encargadoDeLaSucursalUno))
                .isInstanceOf(ForbiddenSucursalException.class);
    }

    @Test
    void unEncargadoSiPuedeCrearMesasEnSuPropiaSucursal() {
        MesaRequest request = MesaRequest.builder()
                .numero(1)
                .capacidad(4)
                .estado(EstadoMesa.LIBRE)
                .sucursalId(1L)
                .build();

        AuthenticatedUser encargadoDeLaSucursalUno =
                new AuthenticatedUser(10L, "encargado@test.com", RoleName.ENCARGADO_TURNO, 1L);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursalUno));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mesa mesa = mesaService.crear(request, encargadoDeLaSucursalUno);

        assertThat(mesa.getSucursal().getId()).isEqualTo(1L);
    }

    @Test
    void unAdministradorPuedeCrearMesasEnCualquierSucursal() {
        MesaRequest request = MesaRequest.builder()
                .numero(1)
                .capacidad(4)
                .estado(EstadoMesa.LIBRE)
                .sucursalId(2L)
                .build();

        AuthenticatedUser administrador = new AuthenticatedUser(20L, "admin@test.com", RoleName.ADMINISTRADOR, null);

        when(sucursalRepository.findById(2L)).thenReturn(Optional.of(sucursalDos));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mesa mesa = mesaService.crear(request, administrador);

        assertThat(mesa.getSucursal().getId()).isEqualTo(2L);
    }
}
