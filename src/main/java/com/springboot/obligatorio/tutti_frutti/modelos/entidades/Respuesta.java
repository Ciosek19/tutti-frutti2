package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jugador_nombre", nullable = false)
    private Jugador jugador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partida_id", nullable = false)
    private PartidaMultijugador partida;

    @OneToMany(mappedBy = "respuestaPadre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<RespuestaIndividual> respuestasIndividuales = new ArrayList<>();

    @Column(nullable = false)
    private Integer puntajeTotal;

    @Column(nullable = false)
    private Boolean validada;

    public Respuesta() {
        this.puntajeTotal = 0;
        this.validada = false;
    }

    public Respuesta(Jugador jugador, PartidaMultijugador partida) {
        this();
        this.jugador = jugador;
        this.partida = partida;
    }

    public void agregarRespuestaIndividual(RespuestaIndividual respuestaIndividual) {
        respuestasIndividuales.add(respuestaIndividual);
        respuestaIndividual.setRespuestaPadre(this);
    }

    public void calcularPuntajeTotal() {
        this.puntajeTotal = respuestasIndividuales.stream()
            .mapToInt(RespuestaIndividual::getPuntos)
            .sum();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public PartidaMultijugador getPartida() {
        return partida;
    }

    public void setPartida(PartidaMultijugador partida) {
        this.partida = partida;
    }

    public List<RespuestaIndividual> getRespuestasIndividuales() {
        return respuestasIndividuales;
    }

    public void setRespuestasIndividuales(List<RespuestaIndividual> respuestasIndividuales) {
        this.respuestasIndividuales = respuestasIndividuales;
    }

    public Integer getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(Integer puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    public Boolean getValidada() {
        return validada;
    }

    public void setValidada(Boolean validada) {
        this.validada = validada;
    }
}
