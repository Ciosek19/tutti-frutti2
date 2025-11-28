package com.springboot.obligatorio.tutti_frutti.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Respuesta;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.PartidaMultijugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;

import java.util.Optional;
import java.util.List;

@Repository
public interface IRespuestaRepositorio extends JpaRepository<Respuesta, Long> {
    Optional<Respuesta> findByPartidaAndJugador(PartidaMultijugador partida, Jugador jugador);
    List<Respuesta> findByPartida(PartidaMultijugador partida);
}
