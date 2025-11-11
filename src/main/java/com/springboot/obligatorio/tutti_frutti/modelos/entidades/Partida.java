package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import java.util.List;

import com.springboot.obligatorio.tutti_frutti.modelos.enums.Dificultad;
import com.springboot.obligatorio.tutti_frutti.servicios.ServicioIA;

public abstract class Partida {
    private Jugador creador;
    private int duracion;
    private ServicioIA servicioIA;
    private int cantidadCategorias;
    private List<String> categorias;
    private String letra;

    // Contructor para Partida Solitario
    public Partida(Jugador creador, Dificultad dif, ServicioIA servicioIA) {
        this.creador = creador;
        this.duracion = dif.getSegundos();
        this.cantidadCategorias = dif.getCantidadCategorias();
        this.servicioIA = servicioIA;
    }

    // Obtener categorias random
    public void asignarCategoriasAleatorias() {
        this.categorias = ConfiguracionJuego.getCategoriasAleatorias(this.cantidadCategorias);
    }
    
    // Obtener letra aleatoria
    public void asignarLetraAleatoria() {
        this.letra = ConfiguracionJuego.getLetraAleatoria();
    }

    public Jugador getCreador() {
        return creador;
    }
    public void setCreador(Jugador creador) {
        this.creador = creador;
    }
    public int getDuracion() {
        return duracion;
    }
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
    public ServicioIA getServicioIA() {
        return servicioIA;
    }
    public void setServicioIA(ServicioIA servicioIA) {
        this.servicioIA = servicioIA;
    }
    public List<String> getCategorias() {
        return categorias;
    }
    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }
    public int getCantidadCategorias() {
        return cantidadCategorias;
    }
    public void setCantidadCategorias(int cantidadCategorias) {
        this.cantidadCategorias = cantidadCategorias;
    }
    public String getLetra() {
        return letra;
    }
    public void setLetra(String letra) {
        this.letra = letra;
    }
}
