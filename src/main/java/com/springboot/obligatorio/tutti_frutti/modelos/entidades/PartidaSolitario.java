package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import com.springboot.obligatorio.tutti_frutti.modelos.enums.Dificultad;
import com.springboot.obligatorio.tutti_frutti.servicios.ServicioIA;

public class PartidaSolitario extends Partida {

    public PartidaSolitario(Jugador creador, Dificultad dif, ServicioIA servicioIA){
        super(creador,dif,servicioIA);
    }
}
