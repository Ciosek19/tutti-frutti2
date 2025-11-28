package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.springboot.obligatorio.tutti_frutti.modelos.enums.Dificultad;

public class PartidaSolitario{
    private Jugador creador;
    private List<String> categorias;
    private String letra;
    private Map<String, String> respuestas;
    private int duracion;
    
    public PartidaSolitario() {}

    public PartidaSolitario(Jugador creador, Dificultad dificultad) {
        this.creador = creador;
        
        this.categorias = asignarCategoriasAleatorias(dificultad.getCantidadCategorias());
        this.duracion = dificultad.getSegundos();
        this.letra = asignarLetraAleatoria();
        this.respuestas = new HashMap<>();
    }
    
    private List<String> asignarCategoriasAleatorias(int cantidad) {
        return ConfiguracionJuego.getCategoriasAleatorias(cantidad);
    }

    private String asignarLetraAleatoria() {
        return ConfiguracionJuego.getLetraAleatoria();
    }


    public Jugador getCreador() {
        return this.creador;
    }

    public void setCreador(Jugador creador) {
        this.creador = creador;
    }

    public List<String> getCategorias() {
        return this.categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public String getLetra() {
        return this.letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public Map<String,String> getRespuestas() {
        return this.respuestas;
    }

    public void setRespuestas(Map<String,String> respuestas) {
        this.respuestas = respuestas;
    }

    public int getDuracion() {
        return this.duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

}