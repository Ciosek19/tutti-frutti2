package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Jugador {
    @Id
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sala_codigo")
    @JsonBackReference
    private Sala sala;

    public Jugador(){}

    public Jugador(String nombre){
        this.nombre = nombre;
    }

    public Sala getSala() {
        return this.sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
