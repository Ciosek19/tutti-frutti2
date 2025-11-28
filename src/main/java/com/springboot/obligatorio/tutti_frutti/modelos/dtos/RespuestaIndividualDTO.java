package com.springboot.obligatorio.tutti_frutti.modelos.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RespuestaIndividualDTO {
    
    @JsonProperty("categoria")
    private String categoria;
    
    @JsonProperty("respuesta")
    private String respuesta;
    
    @JsonProperty("razon")
    private String razon;
    
    @JsonProperty("esValida")
    private boolean esValida;
    
    // Constructores
    public RespuestaIndividualDTO() {
    }
    
    public RespuestaIndividualDTO(String categoria, String respuesta, String razon, boolean esValida) {
        this.categoria = categoria;
        this.respuesta = respuesta;
        this.razon = razon;
        this.esValida = esValida;
    }
    
    // Getters y Setters
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

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public boolean isEsValida() {
        return esValida;
    }

    public void setEsValida(boolean esValida) {
        this.esValida = esValida;
    }
}