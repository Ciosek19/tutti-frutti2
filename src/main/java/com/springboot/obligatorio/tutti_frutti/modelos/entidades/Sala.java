package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.obligatorio.tutti_frutti.modelos.enums.EstadoSala;

@Entity
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String creador;

    @Column(nullable = false)
    private int maxJugadores;
    
    @Column(nullable = false)
    private int cantidadCategorias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSala estado;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Jugador> jugadores = new ArrayList<>();

    public Sala(String nombre, String creador) {
        this.nombre = nombre;
        this.creador = creador;
        this.maxJugadores = 6;
        this.cantidadCategorias = 5;
        this.estado = EstadoSala.ESPERANDO;
    }

    public Sala() {
    };

    public void agregarJugador(Jugador jugador) {
        this.jugadores.add(jugador);
        jugador.setSala(this);
    }

    public void removerJugador(Jugador jugador) {
        this.jugadores.remove(jugador);
        jugador.setSala(null);
    }


    public int getCantidadCategorias() {
        return this.cantidadCategorias;
    }

    public void setCantidadCategorias(int cantidadCategorias) {
        this.cantidadCategorias = cantidadCategorias;
    }
    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public List<Jugador> getJugadores() {
        return this.jugadores;
    }

    public int getCantidadJugadores() {
        return this.jugadores.size();
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCreador() {
        return this.creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public int getMaxJugadores() {
        return this.maxJugadores;
    }

    public void setMaxJugadores(int maxJugadores) {
        this.maxJugadores = maxJugadores;
    }

    public EstadoSala getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoSala estado) {
        this.estado = estado;
    }
}
