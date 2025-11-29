package com.springboot.obligatorio.tutti_frutti.servicios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.obligatorio.tutti_frutti.modelos.dtos.ValidacionItemDTO;
import com.springboot.obligatorio.tutti_frutti.modelos.dtos.ValidacionesDTO;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.PartidaMultijugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Respuesta;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.RespuestaIndividual;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
import com.springboot.obligatorio.tutti_frutti.modelos.enums.EstadoSala;
import com.springboot.obligatorio.tutti_frutti.repositorios.IPartidaMultijugadorRepositorio;
import com.springboot.obligatorio.tutti_frutti.repositorios.IRespuestaRepositorio;
import com.springboot.obligatorio.tutti_frutti.repositorios.ISalaRepositorio;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EvaluacionServicio {

    @Autowired
    private IRespuestaRepositorio respuestaRepositorio;

    @Autowired
    private IPartidaMultijugadorRepositorio partidaRepositorio;

    @Autowired
    private ISalaRepositorio salaRepositorio;

    @Autowired
    private IAServicio iaServicio;

    public List<Respuesta> evaluarPartida(Long partidaId) {

        PartidaMultijugador partida = partidaRepositorio.findById(partidaId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        List<Respuesta> respuestas = respuestaRepositorio.findByPartida(partida);
        String letra = partida.getLetra();


        for (Respuesta respuesta : respuestas) {
            if (!respuesta.getValidada()) {
                validarRespuestaConIA(respuesta, letra);
                respuesta.setValidada(true);
                respuestaRepositorio.save(respuesta);
            }
        }

        partida.setFinalizada(true);
        partidaRepositorio.save(partida);

        Sala sala = partida.getSala();
        sala.setEstado(EstadoSala.ESPERANDO);
        salaRepositorio.save(sala);

        respuestas.sort((r1, r2) -> r2.getPuntajeTotal().compareTo(r1.getPuntajeTotal()));

        return respuestas;
    }

    private void validarRespuestaConIA(Respuesta respuesta, String letra) {

        Map<String, String> respuestasMap = new HashMap<>();

        for (RespuestaIndividual ri : respuesta.getRespuestasIndividuales()) {
            String respuestaTexto = (ri.getRespuesta() != null && !ri.getRespuesta().trim().isEmpty())
                ? ri.getRespuesta()
                : "";
            respuestasMap.put(ri.getCategoria(), respuestaTexto);
        }

        if (respuestasMap.isEmpty()) {
            return;
        }
        ValidacionesDTO validacion = iaServicio.validarRespuestas(respuestasMap, letra);

        for (ValidacionItemDTO item : validacion.getValidaciones()) {
            for (RespuestaIndividual ri : respuesta.getRespuestasIndividuales()) {
                if (ri.getCategoria().equals(item.getCategoria())) {
                    ri.setEsValida(item.getEsValida());
                    ri.setPuntos(item.getPuntos());
                    ri.setRazon(item.getRazon() != null ? item.getRazon() : "");
                    break;
                }
            }
            
        }
        respuesta.calcularPuntajeTotal();
    }
}
