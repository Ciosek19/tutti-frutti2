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
        // Filtrar solo salas con jugadores
        return todasSalas.stream()
                .filter(sala -> !sala.getJugadores().isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean unirseASala(String codigoSala, String nombre) {
        Optional<Sala> salaOpt = salaRepositorio.findById(codigoSala);
        Optional<Jugador> jugadorOpt = jugadorRepositorio.findById(nombre);

        if (!salaOpt.isPresent() || !jugadorOpt.isPresent()) {
            System.out.println("SalaServicio.unirseASala() -> Jugador o Sala = nulo");
            return false;
        }

        Sala sala = salaOpt.get();
        Jugador jugador = jugadorOpt.get();

        if (sala.getEstado() == EstadoSala.JUGANDO) {
            System.out.println("SalaServicio.unirseASala() -> La sala está JUGANDO, no se puede unir");
            return false;
        }

        if (sala.getJugadores().size() >= sala.getMaxJugadores()) {
            System.out.println("SalaServicio.unirseASala() -> La sala está llena");
            return false;
        }

        if (!sala.getJugadores().contains(jugador)) {
            sala.agregarJugador(jugador);
            salaRepositorio.save(sala);
            System.out.println("SalaServicio.unirseASala() -> Jugador" + jugador.getNombre() + " guardado en sala "
                    + sala.getNombre());
        }
        System.out.println("SalaServicio.unirseASala() -> Exito");
        return true;
    }

    public void salirDeSala(String codigoSala, String id) {
        System.out.println("SalaServicio.unirseASala("+codigoSala+","+id+")");
        Sala sala = salaRepositorio.findById(codigoSala).orElse(null);
        Jugador jugador = jugadorRepositorio.findById(id).orElse(null);

        if (sala == null) {
            System.out.println("SalaServicio.unirseASala() -> Sala = nulo");
            return;
        }
        if (jugador == null) {
            System.out.println("SalaServicio.unirseASala() -> Jugador = nulo");
            return;
        }

        if (jugador.getSala() != null) {
            sala = jugador.getSala();
            sala.removerJugador(jugador);
            System.out.println("SalaServicio.salirDeSala() -> Jugador removido de la sala");
        } else {
            System.out.println("SalaServicio.salirDeSala() -> El jugador no estaba en ninguna sala");
            return;
        }

        if (sala.getJugadores().isEmpty()) {
            salaRepositorio.delete(sala);
            return;
        }

        if (sala.getCreador().getId().equals(jugador.getId())) {
            Jugador nuevoCreador = sala.getJugadores().get(0);
            sala.setCreador(nuevoCreador);
            System.out.println("SalaServicio.salirDeSala() -> Nuevo creador: " + nuevoCreador.getNombre());
        }

        salaRepositorio.save(sala);
        System.out.println("SalaServicio.salirDeSala() -> Exito");
    }

    public Sala buscarPorCodigo(String codigo) {
        Optional<Sala> salaOpt = salaRepositorio.findById(codigo);
        if (salaOpt.isPresent()) {
            return salaOpt.get();
        }
        System.out.println("SalaServicio.buscarPorCodigo() -> null");
        return null;
    }

    public Sala guardarSala(Sala sala) {
        return salaRepositorio.save(sala);
    }

}
