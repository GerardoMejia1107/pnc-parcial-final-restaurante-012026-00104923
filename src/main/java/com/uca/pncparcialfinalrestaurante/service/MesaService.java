package com.uca.pncparcialfinalrestaurante.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uca.pncparcialfinalrestaurante.dto.request.MesaRequest;
import com.uca.pncparcialfinalrestaurante.entities.Mesa;
import com.uca.pncparcialfinalrestaurante.entities.Sucursal;
import com.uca.pncparcialfinalrestaurante.exception.ForbiddenSucursalException;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.repository.SucursalRepository;
import com.uca.pncparcialfinalrestaurante.security.AuthenticatedUser;
import com.uca.pncparcialfinalrestaurante.security.RoleName;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final SucursalRepository sucursalRepository;

    public Mesa crear(MesaRequest request, AuthenticatedUser actor) {
        Sucursal sucursal = obtenerSucursal(request.getSucursalId());
        verificarAccesoSucursal(actor, sucursal.getId());

        Mesa mesa = Mesa.builder()
                .numero(request.getNumero())
                .capacidad(request.getCapacidad())
                .estado(request.getEstado())
                .sucursal(sucursal)
                .build();

        return mesaRepository.save(mesa);
    }

    public List<Mesa> listar(AuthenticatedUser actor) {
        if (actor.rol() == RoleName.ENCARGADO_TURNO) {
            return mesaRepository.findBySucursalId(actor.sucursalId());
        }
        return mesaRepository.findAll();
    }

    public Mesa obtenerPorId(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id " + id));
    }

    public Mesa actualizar(Long id, MesaRequest request, AuthenticatedUser actor) {
        Mesa mesa = obtenerPorId(id);
        verificarAccesoSucursal(actor, mesa.getSucursal().getId());

        Sucursal sucursal = mesa.getSucursal();
        if (!sucursal.getId().equals(request.getSucursalId())) {
            sucursal = obtenerSucursal(request.getSucursalId());
            verificarAccesoSucursal(actor, sucursal.getId());
        }

        mesa.setNumero(request.getNumero());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setEstado(request.getEstado());
        mesa.setSucursal(sucursal);
        return mesaRepository.save(mesa);
    }

    public void eliminar(Long id, AuthenticatedUser actor) {
        Mesa mesa = obtenerPorId(id);
        verificarAccesoSucursal(actor, mesa.getSucursal().getId());
        mesaRepository.delete(mesa);
    }

    private Sucursal obtenerSucursal(Long sucursalId) {
        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + sucursalId));
    }

    private void verificarAccesoSucursal(AuthenticatedUser actor, Long sucursalId) {
        if (actor.rol() == RoleName.ENCARGADO_TURNO && !sucursalId.equals(actor.sucursalId())) {
            throw new ForbiddenSucursalException("El encargado no tiene acceso a la sucursal solicitada");
        }
    }
}
