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
import com.springboot.obligatorio.tutti_frutti.utilidades.ConstructorPrompt;

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
        System.out.println("EvaluacionServicio.evaluarPartida(" + partidaId + ")");

        PartidaMultijugador partida = partidaRepositorio.findById(partidaId)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        List<Respuesta> respuestas = respuestaRepositorio.findByPartida(partida);
        String letra = partida.getLetra();

        System.out.println("Total de respuestas encontradas: " + respuestas.size());

        for (Respuesta respuesta : respuestas) {
            System.out.println("\n--- Procesando respuesta de: " + respuesta.getJugador().getNombre() + " ---");
            System.out.println("ID Respuesta: " + respuesta.getId());
            System.out.println("Validada: " + respuesta.getValidada());
            System.out.println("Puntaje actual: " + respuesta.getPuntajeTotal());
            System.out.println("Respuestas individuales en BD: " + respuesta.getRespuestasIndividuales().size());

            if (!respuesta.getValidada()) {
                validarRespuestaConIA(respuesta, letra);
                respuesta.setValidada(true);
                Respuesta respuestaActualizada = respuestaRepositorio.save(respuesta);
                System.out.println("Respuesta actualizada guardada con puntaje: " + respuestaActualizada.getPuntajeTotal());
            } else {
                System.out.println("Respuesta de " + respuesta.getJugador().getNombre() + " ya validada");
            }
        }

        partida.setFinalizada(true);
        partidaRepositorio.save(partida);

        Sala sala = partida.getSala();
        sala.setEstado(EstadoSala.ESPERANDO);
        salaRepositorio.save(sala);
        System.out.println("Sala " + sala.getCodigo() + " liberada, estado: ESPERANDO");

        respuestas.sort((r1, r2) -> r2.getPuntajeTotal().compareTo(r1.getPuntajeTotal()));

        System.out.println("EvaluacionServicio.evaluarPartida() -> Completado");
        return respuestas;
    }

    private void validarRespuestaConIA(Respuesta respuesta, String letra) {
        System.out.println("=== VALIDANDO CON IA ===");
        System.out.println("Jugador: " + respuesta.getJugador().getNombre());
        System.out.println("Respuestas individuales ANTES de validar: " + respuesta.getRespuestasIndividuales().size());

        Map<String, String> respuestasMap = new HashMap<>();

        for (RespuestaIndividual ri : respuesta.getRespuestasIndividuales()) {
            String respuestaTexto = (ri.getRespuesta() != null && !ri.getRespuesta().trim().isEmpty())
                ? ri.getRespuesta()
                : "";
            respuestasMap.put(ri.getCategoria(), respuestaTexto);
            System.out.println("  - " + ri.getCategoria() + ": " + respuestaTexto + " (ID: " + ri.getId() + ")");
        }

        if (respuestasMap.isEmpty()) {
            System.out.println("ERROR: No hay respuestas individuales para validar!");
            return;
        }

        String prompt = ConstructorPrompt.promptValidacionMultijugador(letra, respuestasMap,
                respuesta.getJugador().getNombre());

        System.out.println("Llamando a IA para validar...");
        ValidacionesDTO validacion = iaServicio.validarRespuestas(respuestasMap, letra);
        System.out.println("IA respondió con " + validacion.getValidaciones().size() + " validaciones");

        for (ValidacionItemDTO item : validacion.getValidaciones()) {
            System.out.println("Validación IA - " + item.getCategoria() + ": " + item.getEsValida() + " (" + item.getPuntos() + " pts)");
            boolean encontrado = false;
            for (RespuestaIndividual ri : respuesta.getRespuestasIndividuales()) {
                if (ri.getCategoria().equals(item.getCategoria())) {
                    ri.setEsValida(item.getEsValida());
                    ri.setPuntos(item.getPuntos());
                    ri.setRazon(item.getRazon() != null ? item.getRazon() : "");
                    System.out.println("  Actualizado: " + ri.getCategoria() + " -> esValida=" + ri.getEsValida() + ", puntos=" + ri.getPuntos());
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                System.out.println("  ADVERTENCIA: No se encontró RespuestaIndividual para categoría: " + item.getCategoria());
            }
        }

        respuesta.calcularPuntajeTotal();

        System.out.println("Validación completada para " + respuesta.getJugador().getNombre() +
                ": " + respuesta.getPuntajeTotal() + " puntos");
    }
}
