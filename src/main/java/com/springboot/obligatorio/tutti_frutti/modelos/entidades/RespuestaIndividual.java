package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class RespuestaIndividual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String categoria;

    @Column
    private String respuesta;

    @Column(nullable = false)
    private Boolean esValida;

    @Column(nullable = false)
    private Integer puntos;

    @Column(length = 500)
    private String razon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respuesta_id")
    @JsonBackReference
    private Respuesta respuestaPadre;

    public RespuestaIndividual() {}

    public RespuestaIndividual(String categoria, String respuesta) {
        this.categoria = categoria;
        this.respuesta = respuesta;
        this.esValida = false;
        this.puntos = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public Boolean getEsValida() {
        return esValida;
    }

    public void setEsValida(Boolean esValida) {
        this.esValida = esValida;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public Respuesta getRespuestaPadre() {
        return respuestaPadre;
    }

    public void setRespuestaPadre(Respuesta respuestaPadre) {
        this.respuestaPadre = respuestaPadre;
    }
}
