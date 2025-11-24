package com.springboot.obligatorio.tutti_frutti.servicios.IAGemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.springboot.obligatorio.tutti_frutti.servicios.IAServicio;

@Service
public class ServicioIAGemini extends IAServicio {

    public ServicioIAGemini(@Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.url}") String apiUrl) {
        super(apiKey, apiUrl);
    }

    @Override
    protected String enviarPrompt(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders cabeceras = new HttpHeaders();
            cabeceras.setContentType(MediaType.APPLICATION_JSON);

            // Construir la petición
            GeminiPeticion peticion = new GeminiPeticion();
            GeminiPeticion.Contenido contenido = new GeminiPeticion.Contenido();
            contenido.getParts().add(new GeminiPeticion.Parte(prompt));
            peticion.getContents().add(contenido);

            HttpEntity<GeminiPeticion> entidad = new HttpEntity<>(peticion, cabeceras);

            String urlCompleta = API_URL + "?key=" + API_KEY;

            ResponseEntity<GeminiRespuestaDTO> respuesta = restTemplate.exchange(
                urlCompleta,
                HttpMethod.POST,
                entidad,
                GeminiRespuestaDTO.class
            );

            if (respuesta.getBody() != null &&
                respuesta.getBody().getCandidatos() != null &&
                !respuesta.getBody().getCandidatos().isEmpty() &&
                respuesta.getBody().getCandidatos().get(0).getContenido() != null &&
                !respuesta.getBody().getCandidatos().get(0).getContenido().getPartes().isEmpty()) {

                return respuesta.getBody()
                    .getCandidatos()
                    .get(0)
                    .getContenido()
                    .getPartes()
                    .get(0)
                    .getTexto();
            } else {
                return "No se recibió respuesta de la IA";
            }

        } catch (Exception e) {
            throw new RuntimeException("Error llamando a Gemini API: " + e.getMessage(), e);
        }
    }

}
