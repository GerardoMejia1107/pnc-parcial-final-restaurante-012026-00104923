package com.uca.pncparcialfinalrestaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.pncparcialfinalrestaurante.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
