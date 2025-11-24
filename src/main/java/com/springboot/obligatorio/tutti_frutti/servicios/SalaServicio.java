package com.springboot.obligatorio.tutti_frutti.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
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

    public Sala crearSala(String nombre, String creador) {
        Sala sala = new Sala(nombre, creador);
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
        return salaRepositorio.findAll();
    }

    public boolean unirseASala(String codigoSala, String nombre) {
        Optional<Sala> salaOpt = salaRepositorio.findById(codigoSala);
        Optional<Jugador> jugadorOpt = jugadorRepositorio.findById(nombre);

        // Verifica ambos de una vez
        if (!salaOpt.isPresent() || !jugadorOpt.isPresent()) {
            System.out.println("SalaServicio.unirseASala() -> Jugador o Sala = nulo");
            return false;
        }

        Sala sala = salaOpt.get();
        Jugador jugador = jugadorOpt.get();

        if (!sala.getJugadores().contains(jugador)) {
            sala.agregarJugador(jugador);
            salaRepositorio.save(sala);
            System.out.println("SalaServicio.unirseASala() -> Jugador" + jugador.getNombre() + " guardado en sala "
                    + sala.getNombre());
        }
        System.out.println("SalaServicio.unirseASala() -> Exito");
        return true;
    }

    public void salirDeSala(String codigoSala, String nombre) {
        System.out.println("SalaServicio.unirseASala("+codigoSala+","+nombre+")");
        Optional<Sala> salaOpt = salaRepositorio.findById(codigoSala);
        Optional<Jugador> jugadorOpt = jugadorRepositorio.findById(nombre);

        if (!salaOpt.isPresent()) {
            System.out.println("SalaServicio.unirseASala() -> Sala = nulo");
            return;
        }
        if (!jugadorOpt.isPresent()) {
            System.out.println("SalaServicio.unirseASala() -> Jugador = nulo");
            return;
        }

        Sala sala = salaOpt.get();
        Jugador jugador = jugadorOpt.get();

        if (jugador.getSala() != null) {
            sala = jugador.getSala();
            sala.removerJugador(jugador);
            jugadorRepositorio.save(jugador);
            System.out.println("SalaServicio.salirDeSala() -> Exito");
        } else {
            System.out.println("SalaServicio.salirDeSala() -> Sala = nulo");
        }

        if (sala.getJugadores().isEmpty()) {
            eliminarSala(codigoSala);
            return;
        }
        
        if (sala.getCreador().equals(nombre)) {
            Jugador nuevoCreador = sala.getJugadores().get(0);
            sala.setCreador(nuevoCreador.getNombre());
            salaRepositorio.save(sala);
            System.out.println("SalaServicio.salirDeSala() -> Nuevo creador: " + nuevoCreador.getNombre());
        }
    }

    public Sala buscarPorCodigo(String codigo) {
        Optional<Sala> salaOpt = salaRepositorio.findById(codigo);
        if (salaOpt.isPresent()) {
            return salaOpt.get();
        }
        System.out.println("SalaServicio.buscarPorCodigo() -> null");
        return null;
    }

}
