package com.springboot.obligatorio.tutti_frutti.modelos.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ValidacionesDTO {
    
    @JsonProperty("validaciones")
    private List<ValidacionItemDTO> validaciones;
    
    @JsonProperty("puntajeTotal")
    private Integer puntajeTotal;
    
    @JsonProperty("respuestasValidas")
    private Integer respuestasValidas;
    
    @JsonProperty("respuestasInvalidas")
    private Integer respuestasInvalidas;
    
    public ValidacionesDTO() {}
    
    // Getters y Setters
    public List<ValidacionItemDTO> getValidaciones() {
        return validaciones;
    }
    
    public void setValidaciones(List<ValidacionItemDTO> validaciones) {
        this.validaciones = validaciones;
    }
    
    public Integer getPuntajeTotal() {
        return puntajeTotal;
    }
    
    public void setPuntajeTotal(Integer puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }
    
    public Integer getRespuestasValidas() {
        return respuestasValidas;
    }
    
    public void setRespuestasValidas(Integer respuestasValidas) {
        this.respuestasValidas = respuestasValidas;
    }
    
    public Integer getRespuestasInvalidas() {
        return respuestasInvalidas;
    }
    
    public void setRespuestasInvalidas(Integer respuestasInvalidas) {
        this.respuestasInvalidas = respuestasInvalidas;
    }
}