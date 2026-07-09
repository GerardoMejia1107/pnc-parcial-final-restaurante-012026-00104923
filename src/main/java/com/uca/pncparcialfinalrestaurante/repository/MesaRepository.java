package com.uca.pncparcialfinalrestaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.pncparcialfinalrestaurante.entities.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    List<Mesa> findBySucursalId(Long sucursalId);
}
