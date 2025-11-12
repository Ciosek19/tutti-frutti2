package com.springboot.obligatorio.tutti_frutti.utilidades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.obligatorio.tutti_frutti.modelos.dtos.ValidacionesDTO;

public final class Mapeador {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ValidacionesDTO parsearValidaciones(String textoRespuesta) {
        try {

            ValidacionesDTO respuesta = objectMapper.readValue(textoRespuesta, ValidacionesDTO.class);

            if (respuesta.getValidaciones() == null) {
                throw new RuntimeException("Respuesta parseada tiene validaciones null");
            }

            return respuesta;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error parseando validación.\n" +
                            "Texto recibido: [" + textoRespuesta + "]\n" +
                            "Error: " + e.getMessage(),
                    e);
        }
    }
}
