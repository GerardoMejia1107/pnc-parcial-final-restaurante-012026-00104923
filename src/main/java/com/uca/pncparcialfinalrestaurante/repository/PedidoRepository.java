package com.uca.pncparcialfinalrestaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.pncparcialfinalrestaurante.entities.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByMesaSucursalId(Long sucursalId);
}
