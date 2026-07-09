package com.uca.pncparcialfinalrestaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.pncparcialfinalrestaurante.entities.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}
