package com.uca.pncparcialfinalrestaurante.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.uca.pncparcialfinalrestaurante.dto.request.ActualizarEstadoPedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.DetallePedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.PedidoRequest;
import com.uca.pncparcialfinalrestaurante.entities.DetallePedido;
import com.uca.pncparcialfinalrestaurante.entities.EstadoPedido;
import com.uca.pncparcialfinalrestaurante.entities.Mesa;
import com.uca.pncparcialfinalrestaurante.entities.Pedido;
import com.uca.pncparcialfinalrestaurante.entities.Producto;
import com.uca.pncparcialfinalrestaurante.entities.Usuario;
import com.uca.pncparcialfinalrestaurante.exception.ForbiddenSucursalException;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.repository.PedidoRepository;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public Pedido crear(PedidoRequest request, AuthenticatedUser actor) {
        Mesa mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id " + request.getMesaId()));

        Usuario cliente = usuarioRepository.findById(actor.id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + actor.id()));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .mesa(mesa)
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        request.getDetalles().stream()
                .map(item -> construirDetalle(pedido, item))
                .forEach(detalle -> pedido.getDetalles().add(detalle));

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listar(AuthenticatedUser actor) {
        return switch (actor.rol()) {
            case ADMINISTRADOR -> pedidoRepository.findAll();
            case ENCARGADO_TURNO -> pedidoRepository.findByMesaSucursalId(actor.sucursalId());
            case CLIENTE -> pedidoRepository.findByClienteId(actor.id());
        };
    }

    public Pedido obtenerPorId(Long id, AuthenticatedUser actor) {
        Pedido pedido = buscar(id);
        verificarAcceso(pedido, actor);
        return pedido;
    }

    public Pedido actualizarEstado(Long id, ActualizarEstadoPedidoRequest request, AuthenticatedUser actor) {
        Pedido pedido = buscar(id);
        verificarGestion(pedido, actor);
        pedido.setEstado(request.getEstado());
        return pedidoRepository.save(pedido);
    }

    public Pedido cancelar(Long id, AuthenticatedUser actor) {
        Pedido pedido = buscar(id);
        verificarAcceso(pedido, actor);
        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    private DetallePedido construirDetalle(Pedido pedido, DetallePedidoRequest item) {
        Producto producto = productoRepository.findById(item.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + item.getProductoId()));

        return DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(item.getCantidad())
                .precioUnitario(producto.getPrecio())
                .build();
    }

    private Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + id));
    }

    private void verificarAcceso(Pedido pedido, AuthenticatedUser actor) {
        switch (actor.rol()) {
            case ADMINISTRADOR -> { }
            case ENCARGADO_TURNO -> verificarSucursal(pedido, actor);
            case CLIENTE -> verificarPropietario(pedido, actor);
        }
    }

    private void verificarGestion(Pedido pedido, AuthenticatedUser actor) {
        switch (actor.rol()) {
            case ADMINISTRADOR -> { }
            case ENCARGADO_TURNO -> verificarSucursal(pedido, actor);
            case CLIENTE -> throw new AccessDeniedException("Un cliente no puede gestionar el estado del pedido");
        }
    }

    private void verificarSucursal(Pedido pedido, AuthenticatedUser actor) {
        Long sucursalPedido = pedido.getMesa().getSucursal().getId();
        if (!sucursalPedido.equals(actor.sucursalId())) {
            throw new ForbiddenSucursalException("El encargado no tiene acceso a la sucursal de este pedido");
        }
    }

    private void verificarPropietario(Pedido pedido, AuthenticatedUser actor) {
        if (!pedido.getCliente().getId().equals(actor.id())) {
            throw new AccessDeniedException("El cliente solo puede acceder a sus propios pedidos");
        }
    }
}
