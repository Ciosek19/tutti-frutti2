package com.springboot.obligatorio.tutti_frutti.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.PartidaMultijugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;

import java.util.Optional;

@Repository
public interface IPartidaMultijugadorRepositorio extends JpaRepository<PartidaMultijugador, Long> {
    Optional<PartidaMultijugador> findBySalaAndFinalizadaFalse(Sala sala);
}
