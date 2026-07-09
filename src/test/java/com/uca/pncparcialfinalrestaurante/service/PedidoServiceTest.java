package com.uca.pncparcialfinalrestaurante.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.uca.pncparcialfinalrestaurante.dto.request.ActualizarEstadoPedidoRequest;
import com.uca.pncparcialfinalrestaurante.entities.EstadoPedido;
import com.uca.pncparcialfinalrestaurante.entities.Mesa;
import com.uca.pncparcialfinalrestaurante.entities.Pedido;
import com.uca.pncparcialfinalrestaurante.entities.Sucursal;
import com.uca.pncparcialfinalrestaurante.entities.Usuario;
import com.uca.pncparcialfinalrestaurante.exception.ForbiddenSucursalException;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.repository.PedidoRepository;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.AuthenticatedUser;
import com.uca.pncparcialfinalrestaurante.security.RoleName;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoDeLaSucursalUno;

    @BeforeEach
    void setUp() {
        Sucursal sucursalUno = Sucursal.builder().id(1L).nombre("Sucursal Centro").build();
        Mesa mesa = Mesa.builder().id(5L).numero(3).sucursal(sucursalUno).build();
        Usuario cliente = Usuario.builder().id(100L).nombreCompleto("Cliente Uno").rol(RoleName.CLIENTE).build();

        pedidoDeLaSucursalUno = Pedido.builder()
                .id(1L)
                .cliente(cliente)
                .mesa(mesa)
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void unEncargadoDeOtraSucursalNoPuedeVerElPedido() {
        AuthenticatedUser encargadoDeOtraSucursal =
                new AuthenticatedUser(50L, "encargado@test.com", RoleName.ENCARGADO_TURNO, 2L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDeLaSucursalUno));

        assertThatThrownBy(() -> pedidoService.obtenerPorId(1L, encargadoDeOtraSucursal))
                .isInstanceOf(ForbiddenSucursalException.class);
    }

    @Test
    void unEncargadoDeLaMismaSucursalSiPuedeVerElPedido() {
        AuthenticatedUser encargadoDeLaSucursalUno =
                new AuthenticatedUser(51L, "encargado2@test.com", RoleName.ENCARGADO_TURNO, 1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDeLaSucursalUno));

        Pedido pedido = pedidoService.obtenerPorId(1L, encargadoDeLaSucursalUno);

        assertThat(pedido.getId()).isEqualTo(1L);
    }

    @Test
    void unAdministradorPuedeVerCualquierPedidoSinImportarLaSucursal() {
        AuthenticatedUser administrador = new AuthenticatedUser(1L, "admin@test.com", RoleName.ADMINISTRADOR, null);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDeLaSucursalUno));

        Pedido pedido = pedidoService.obtenerPorId(1L, administrador);

        assertThat(pedido.getId()).isEqualTo(1L);
    }

    @Test
    void unClienteNoPuedeVerElPedidoDeOtroCliente() {
        AuthenticatedUser otroCliente = new AuthenticatedUser(200L, "otro@test.com", RoleName.CLIENTE, null);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDeLaSucursalUno));

        assertThatThrownBy(() -> pedidoService.obtenerPorId(1L, otroCliente))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void unClienteSiPuedeVerSuPropioPedido() {
        AuthenticatedUser mismoCliente = new AuthenticatedUser(100L, "cliente@test.com", RoleName.CLIENTE, null);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDeLaSucursalUno));

        Pedido pedido = pedidoService.obtenerPorId(1L, mismoCliente);

        assertThat(pedido.getId()).isEqualTo(1L);
    }

    @Test
    void unClienteNoPuedeCambiarElEstadoDeUnPedido() {
        AuthenticatedUser mismoCliente = new AuthenticatedUser(100L, "cliente@test.com", RoleName.CLIENTE, null);
        ActualizarEstadoPedidoRequest request =
                ActualizarEstadoPedidoRequest.builder().estado(EstadoPedido.CONFIRMADO).build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDeLaSucursalUno));

        assertThatThrownBy(() -> pedidoService.actualizarEstado(1L, request, mismoCliente))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void unEncargadoDeLaMismaSucursalPuedeCambiarElEstadoDeUnPedido() {
        AuthenticatedUser encargadoDeLaSucursalUno =
                new AuthenticatedUser(51L, "encargado2@test.com", RoleName.ENCARGADO_TURNO, 1L);
        ActualizarEstadoPedidoRequest request =
                ActualizarEstadoPedidoRequest.builder().estado(EstadoPedido.CONFIRMADO).build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoDeLaSucursalUno));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido pedido = pedidoService.actualizarEstado(1L, request, encargadoDeLaSucursalUno);

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
    }
}
