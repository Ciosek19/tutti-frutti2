package com.springboot.obligatorio.tutti_frutti.servicios;

import java.util.Map;


import com.springboot.obligatorio.tutti_frutti.modelos.dtos.ValidacionesDTO;
import com.springboot.obligatorio.tutti_frutti.utilidades.ConstructorPrompt;
import com.springboot.obligatorio.tutti_frutti.utilidades.Mapeador;


public abstract class ServicioIA {

    protected String API_KEY;
    protected String API_URL;

    protected ServicioIA(String apiKey, String apiUrl) {
        this.API_KEY = apiKey;
        this.API_URL = apiUrl;
    }

    public ValidacionesDTO validarRespuestas(Map<String,String> respuestas, String letra) {
        try {
            // 1. Construir el prompt
            String prompt = ConstructorPrompt.promptValidacionSolitario(letra, respuestas);
            
            System.out.println("📤 Solicitando validación a la IA para letra: " + letra);
            System.out.println("   Respuestas: " + respuestas);
            
            // 2. Enviar el prompt a la IA y obtener la respuesta
            String textoRespuesta = enviarPrompt(prompt);

            // 3. Parsear String a Objeto Java
            ValidacionesDTO validacion = Mapeador.parsearValidaciones(textoRespuesta);
            
            System.out.println("Validacion completada - Puntaje: " + validacion.getPuntajeTotal());
            System.out.println("Validas: " + validacion.getRespuestasValidas() + 
                             " | Inválidas: " + validacion.getRespuestasInvalidas());
            
            // 4. Devolver objeto listo
            return validacion;
            
        } catch (Exception e) {
            System.err.println("Error validando respuestas: " + e.getMessage());
            throw new RuntimeException("Error al validar respuestas: " + e.getMessage(), e);
        }
    }

    protected abstract String enviarPrompt(String prompt);
    
}
