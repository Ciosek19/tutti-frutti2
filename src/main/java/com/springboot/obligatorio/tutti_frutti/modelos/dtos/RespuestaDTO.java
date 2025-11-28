package com.springboot.obligatorio.tutti_frutti.modelos.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class RespuestaDTO {
    
    @JsonProperty("idJugador")
    private String idJugador; // Nombre del jugador
    
    @JsonProperty("letra")
    private String letra;
    
    @JsonProperty("respuestas")
    private List<RespuestaIndividualDTO> respuestas = new ArrayList<>();
    
    @JsonProperty("puntajeTotal")
    private Integer puntajeTotal;
    
    // Constructores
    public RespuestaDTO() {
    }
    
    public RespuestaDTO(String nombreJugador, String letra, 
                        List<RespuestaIndividualDTO> respuestas, 
                        Integer puntajeTotal) {
        this.idJugador = nombreJugador;
        this.letra = letra;
        this.respuestas = respuestas;
        this.puntajeTotal = puntajeTotal;
    }
    
    // Métodos de utilidad
    public int getRespuestasValidas() {
        return (int) respuestas.stream()
                .filter(RespuestaIndividualDTO::isEsValida)
                .count();
    }
    
    public int getRespuestasInvalidas() {
        return respuestas.size() - getRespuestasValidas();
    }
    
    public List<String> getCategoriasCorrectas() {
        return respuestas.stream()
                .filter(RespuestaIndividualDTO::isEsValida)
                .map(RespuestaIndividualDTO::getCategoria)
                .toList();
    }
    
    public List<String> getCategoriasIncorrectas() {
        return respuestas.stream()
                .filter(r -> !r.isEsValida())
                .map(RespuestaIndividualDTO::getCategoria)
                .toList();
    }
    
    // Alias para mejor legibilidad en vistas
    public String getNombreJugador() {
        return idJugador;
    }
    
    public void setNombreJugador(String nombreJugador) {
        this.idJugador = nombreJugador;
    }
    
    // Getters y Setters
    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String idJugador) {
        this.idJugador = idJugador;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public List<RespuestaIndividualDTO> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<RespuestaIndividualDTO> respuestas) {
        this.respuestas = respuestas;
    }

    public Integer getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(Integer puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }
}