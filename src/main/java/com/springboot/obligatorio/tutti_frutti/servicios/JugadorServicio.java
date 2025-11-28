package com.springboot.obligatorio.tutti_frutti.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.repositorios.IJugadorRepositorio;

@Service
public class JugadorServicio {

    @Autowired
    private IJugadorRepositorio jugadorRepositorio;

    public Jugador obtenerJugador(String id){
        return jugadorRepositorio.findById(id).orElse(null);
    }

    public boolean existeNombre(String nombre) {
        return jugadorRepositorio.existsByNombre(nombre);
    }

    public Jugador crearJugador(String nombre) {
        Jugador jugador = new Jugador(nombre);
        return jugadorRepositorio.save(jugador);
    }

    public void eliminarJugador(String id) {
        Jugador jugador = obtenerJugador(id);
        if (jugador != null) {
            jugadorRepositorio.delete(jugador);
        }
    }
}
