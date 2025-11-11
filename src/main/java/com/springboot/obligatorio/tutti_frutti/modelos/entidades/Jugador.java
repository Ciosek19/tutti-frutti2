package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

public class Jugador {
    private String nombre;
    private String uuid;

    public Jugador(String nombre, String uuid) {
        this.nombre = nombre;
        this.uuid = uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
