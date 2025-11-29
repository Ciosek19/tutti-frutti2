package com.springboot.obligatorio.tutti_frutti.modelos.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidacionItemDTO {
    
    @JsonProperty("categoria")
    private String categoria;
    
    @JsonProperty("respuestaUsuario")
    private String respuestaUsuario;
    
    @JsonProperty("esValida")
    private Boolean esValida;
    
    @JsonProperty("puntos")
    private Integer puntos;
    
    @JsonProperty("razon")
    private String razon;
    
    public ValidacionItemDTO() {}
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getRespuestaUsuario() {
        return respuestaUsuario;
    }
    
    public void setRespuestaUsuario(String respuestaUsuario) {
        this.respuestaUsuario = respuestaUsuario;
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
}