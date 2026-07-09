package com.uca.pncparcialfinalrestaurante.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uca.pncparcialfinalrestaurante.dto.request.SucursalRequest;
import com.uca.pncparcialfinalrestaurante.entities.Sucursal;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.repository.SucursalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public Sucursal crear(SucursalRequest request) {
        Sucursal sucursal = Sucursal.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .build();

        return sucursalRepository.save(sucursal);
    }

    public List<Sucursal> listar() {
        return sucursalRepository.findAll();
    }

    public Sucursal obtenerPorId(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + id));
    }

    public Sucursal actualizar(Long id, SucursalRequest request) {
        Sucursal sucursal = obtenerPorId(id);
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        return sucursalRepository.save(sucursal);
    }

    public void eliminar(Long id) {
        sucursalRepository.delete(obtenerPorId(id));
    }
}
