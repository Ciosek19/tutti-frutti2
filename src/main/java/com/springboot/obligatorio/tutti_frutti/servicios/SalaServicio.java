package com.springboot.obligatorio.tutti_frutti.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
import com.springboot.obligatorio.tutti_frutti.modelos.enums.EstadoSala;
import com.springboot.obligatorio.tutti_frutti.repositorios.IJugadorRepositorio;
import com.springboot.obligatorio.tutti_frutti.repositorios.ISalaRepositorio;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SalaServicio {

    @Autowired
    private ISalaRepositorio salaRepositorio;

    @Autowired
    private IJugadorRepositorio jugadorRepositorio;

    public Sala crearSala(String nombre, Jugador creador) {
        Sala sala = new Sala(nombre, creador);
        sala.agregarJugador(creador);
        return salaRepositorio.save(sala);
    }

    public Sala eliminarSala(String codigo){
        Optional<Sala> salaOpt = salaRepositorio.findById(codigo);
        if (salaOpt.isPresent()) {
            salaRepositorio.delete(salaOpt.get());
        }
        return null;
    }

    public List<Sala> obtenerSalas() {
        List<Sala> todasSalas = salaRepositorio.findAll();
        return todasSalas.stream()
                .filter(sala -> !sala.getJugadores().isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean unirseASala(String codigoSala, String nombre) {
        Optional<Sala> salaOpt = salaRepositorio.findById(codigoSala);
        Optional<Jugador> jugadorOpt = jugadorRepositorio.findById(nombre);

        if (!salaOpt.isPresent() || !jugadorOpt.isPresent()) {
            return false;
        }

        Sala sala = salaOpt.get();
        Jugador jugador = jugadorOpt.get();

        if (sala.getEstado() == EstadoSala.JUGANDO) {
            return false;
        }

        if (sala.getJugadores().size() >= sala.getMaxJugadores()) {
            return false;
        }

        if (!sala.getJugadores().contains(jugador)) {
            sala.agregarJugador(jugador);
            salaRepositorio.save(sala);
        }
        return true;
    }

    public void salirDeSala(String codigoSala, String id) {
        Sala sala = salaRepositorio.findById(codigoSala).orElse(null);
        Jugador jugador = jugadorRepositorio.findById(id).orElse(null);

        if (sala == null) {
            return;
        }
        if (jugador == null) {
            return;
        }

        if (jugador.getSala() != null) {
            sala = jugador.getSala();
            sala.removerJugador(jugador);
        } else {
            return;
        }

        if (sala.getJugadores().isEmpty()) {
            salaRepositorio.delete(sala);
            return;
        }

        if (sala.getCreador().getId().equals(jugador.getId())) {
            Jugador nuevoCreador = sala.getJugadores().get(0);
            sala.setCreador(nuevoCreador);
        }

        salaRepositorio.save(sala);
    }

    public Sala buscarPorCodigo(String codigo) {
        Optional<Sala> salaOpt = salaRepositorio.findById(codigo);
        if (salaOpt.isPresent()) {
            return salaOpt.get();
        }
        return null;
    }

    public Sala guardarSala(Sala sala) {
        return salaRepositorio.save(sala);
    }

}
