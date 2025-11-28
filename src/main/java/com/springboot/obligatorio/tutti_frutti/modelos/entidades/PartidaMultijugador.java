package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

@Entity
public class PartidaMultijugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sala_codigo", nullable = false)
    private Sala sala;

    @Column(nullable = false)
    private int duracion;

    @Column(nullable = false)
    private int cantidadCategorias;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "partida_categorias", joinColumns = @JoinColumn(name = "partida_id"))
    @Column(name = "categoria")
    private List<String> categorias = new ArrayList<>();

    @Column(nullable = false, length = 1)
    private String letra;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Respuesta> respuestas = new ArrayList<>();

    @Column(nullable = false)
    private Boolean finalizada;
    

    public PartidaMultijugador() {
        this.finalizada = false;
    }

    public PartidaMultijugador(Sala sala, int duracion, int cantidadCategorias) {
        this();
        this.sala = sala;
        this.duracion = duracion;
        this.cantidadCategorias = cantidadCategorias;
    }

    public void asignarCategoriasAleatorias() {
        this.categorias = ConfiguracionJuego.getCategoriasAleatorias(this.cantidadCategorias);
    }

    public void asignarLetraAleatoria() {
        this.letra = ConfiguracionJuego.getLetraAleatoria();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getCantidadCategorias() {
        return cantidadCategorias;
    }

    public void setCantidadCategorias(int cantidadCategorias) {
        this.cantidadCategorias = cantidadCategorias;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }

    public Boolean getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(Boolean finalizada) {
        this.finalizada = finalizada;
    }
}
