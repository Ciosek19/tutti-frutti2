package com.springboot.obligatorio.tutti_frutti.servicios;

import java.util.Map;


import com.springboot.obligatorio.tutti_frutti.modelos.dtos.ValidacionesDTO;
import com.springboot.obligatorio.tutti_frutti.utilidades.ConstructorPrompt;
import com.springboot.obligatorio.tutti_frutti.utilidades.Mapeador;


public abstract class IAServicio {

    protected String API_KEY;
    protected String API_URL;

    protected IAServicio(String apiKey, String apiUrl) {
        this.API_KEY = apiKey;
        this.API_URL = apiUrl;
    }

    public ValidacionesDTO validarRespuestas(Map<String,String> respuestas, String letra) {
        try {
            String prompt = ConstructorPrompt.promptValidacionSolitario(letra, respuestas);
            
            String textoRespuesta = enviarPrompt(prompt);

            ValidacionesDTO validacion = Mapeador.parsearValidaciones(textoRespuesta);
            
            return validacion;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al validar respuestas: " + e.getMessage(), e);
        }
    }

    protected abstract String enviarPrompt(String prompt);
    
}
