package com.uca.pncparcialfinalrestaurante.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uca.pncparcialfinalrestaurante.dto.request.ProductoRequest;
import com.uca.pncparcialfinalrestaurante.entities.Producto;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public Producto crear(ProductoRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .disponible(request.isDisponible())
                .build();

        return productoRepository.save(producto);
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerPorId(id);
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setDisponible(request.isDisponible());
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.delete(obtenerPorId(id));
    }
}
