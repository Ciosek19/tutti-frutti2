package com.springboot.obligatorio.tutti_frutti.controladores;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.PartidaMultijugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Respuesta;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.RespuestaIndividual;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
import com.springboot.obligatorio.tutti_frutti.modelos.enums.EstadoSala;
import com.springboot.obligatorio.tutti_frutti.repositorios.IPartidaMultijugadorRepositorio;
import com.springboot.obligatorio.tutti_frutti.repositorios.IRespuestaRepositorio;
import com.springboot.obligatorio.tutti_frutti.repositorios.ISalaRepositorio;
import com.springboot.obligatorio.tutti_frutti.servicios.EvaluacionServicio;
import com.springboot.obligatorio.tutti_frutti.servicios.JugadorServicio;

import jakarta.servlet.http.HttpSession;

@Controller
public class PartidaMultijugadorController {

    @Autowired
    private ISalaRepositorio salaRepositorio;

    @Autowired
    private IPartidaMultijugadorRepositorio partidaRepositorio;

    @Autowired
    private IRespuestaRepositorio respuestaRepositorio;

    @Autowired
    private JugadorServicio jugadorServicio;

    @Autowired
    private EvaluacionServicio evaluacionServicio;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/sala/empezar-partida")
    public String empezarPartida(@RequestParam String codigoSala, HttpSession session) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }
        Sala sala = salaRepositorio.findById(codigoSala).orElse(null);

        if (sala == null || !sala.getCreador().equals(jugador)) {
            return "redirect:/lobby";
        }

        if (sala.getJugadores().size() < 2) {
            return "redirect:/lobby/sala/" + codigoSala;
        }

        sala.setEstado(EstadoSala.JUGANDO);
        salaRepositorio.save(sala);

        PartidaMultijugador partida = new PartidaMultijugador(sala, sala.getDuracion(), sala.getCantidadCategorias());
        partida.asignarCategoriasAleatorias();
        partida.asignarLetraAleatoria();
        partidaRepositorio.save(partida);

        messagingTemplate.convertAndSend("/topic/sala/" + codigoSala,
            Map.of("accion", "INICIAR_PARTIDA", "partidaId", partida.getId()));

        return "redirect:/partida/" + partida.getId();
    }

    @GetMapping("/partida/{partidaId}")
    public String mostrarPartida(@PathVariable Long partidaId, HttpSession session, Model model) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }

        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null || !partida.getSala().getJugadores().contains(jugador)) {
            return "redirect:/lobby";
        }

        model.addAttribute("partida", partida);
        model.addAttribute("nombreJugador", jugador.getNombre());
        model.addAttribute("totalJugadores", partida.getSala().getJugadores().size());
        return "partidaMultijugador";
    }

    @MessageMapping("/tutti-frutti/{partidaId}")
    public void tuttiFrutti(@DestinationVariable Long partidaId) {
        messagingTemplate.convertAndSend("/topic/partida/" + partidaId,
            Map.of("accion", "TERMINAR_PARTIDA"));
    }

    @PostMapping("/partida/{partidaId}/enviar-respuestas")
    public String enviarRespuestas(
            @PathVariable Long partidaId,
            @RequestParam Map<String, String> respuestasMap,
            HttpSession session) {

        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null || !partida.getSala().getJugadores().contains(jugador)) {
            return "redirect:/lobby";
        }

        respuestasMap.remove("letra");
        respuestasMap.remove("partidaId");

        Respuesta respuesta = respuestaRepositorio.findByPartidaAndJugador(partida, jugador)
                .orElse(null);

        if (respuesta != null) {
            respuestaRepositorio.delete(respuesta);
            respuestaRepositorio.flush();
        }

        respuesta = new Respuesta(jugador, partida);

        for (Map.Entry<String, String> entry : respuestasMap.entrySet()) {
            String categoria = entry.getKey();
            String respuestaTexto = entry.getValue();
            RespuestaIndividual respuestaInd = new RespuestaIndividual(categoria, respuestaTexto);
            respuesta.agregarRespuestaIndividual(respuestaInd);
        }

        respuestaRepositorio.save(respuesta);

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);
        int totalJugadores = partida.getSala().getJugadores().size();

        if (todasRespuestas.size() >= totalJugadores && !partida.getFinalizada()) {
            evaluacionServicio.evaluarPartida(partidaId);
            return "redirect:/partida/" + partidaId + "/resultados";
        }

        return "redirect:/partida/" + partidaId + "/esperando-resultados";
    }

    @GetMapping("/partida/{partidaId}/esperando-resultados")
    public String esperandoResultados(@PathVariable Long partidaId, HttpSession session, Model model) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null) {
            return "redirect:/lobby";
        }

        Respuesta respuestaJugador = respuestaRepositorio.findByPartidaAndJugador(partida, jugador).orElse(null);
        if (respuestaJugador == null) {
            return "redirect:/lobby";
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);
        int totalJugadores = partida.getSala().getJugadores().size();

        if (partida.getFinalizada()) {
            return "redirect:/partida/" + partidaId + "/resultados";
        }

        model.addAttribute("partidaId", partidaId);
        model.addAttribute("totalJugadores", totalJugadores);
        model.addAttribute("respuestasRecibidas", todasRespuestas.size());

        return "esperandoResultados";
    }

    @GetMapping("/partida/{partidaId}/estado")
    @ResponseBody
    public Map<String, Object> obtenerEstadoPartida(@PathVariable Long partidaId) {
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null) {
            return Map.of("error", "Partida no encontrada");
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);

        return Map.of(
            "finalizada", partida.getFinalizada(),
            "respuestasRecibidas", todasRespuestas.size(),
            "totalJugadores", partida.getSala().getJugadores().size()
        );
    }

    @PostMapping("/partida/{partidaId}/forzar-evaluacion")
    @ResponseBody
    public Map<String, Object> forzarEvaluacion(@PathVariable Long partidaId) {
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null) {
            return Map.of("error", "Partida no encontrada");
        }

        if (partida.getFinalizada()) {
            return Map.of("mensaje", "Partida ya finalizada");
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);

        if (todasRespuestas.isEmpty()) {
            return Map.of("error", "No hay respuestas para evaluar");
        }

        evaluacionServicio.evaluarPartida(partidaId);

        return Map.of(
            "mensaje", "Evaluación forzada exitosamente",
            "respuestasEvaluadas", todasRespuestas.size()
        );
    }

    @GetMapping("/partida/{partidaId}/debug")
    @ResponseBody
    public Map<String, Object> debugPartida(@PathVariable Long partidaId) {
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null) {
            return Map.of("error", "Partida no encontrada");
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);

        return Map.of(
            "partidaId", partidaId,
            "letra", partida.getLetra(),
            "finalizada", partida.getFinalizada(),
            "totalRespuestas", todasRespuestas.size(),
            "mensaje", "Ver logs en consola para detalles completos"
        );
    }

    @GetMapping("/partida/{partidaId}/resultados")
    public String mostrarResultados(@PathVariable Long partidaId, HttpSession session, Model model) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null) {
            return "redirect:/lobby";
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);

        if (todasRespuestas.isEmpty() && !partida.getFinalizada()) {
            return "redirect:/partida/" + partidaId + "/esperando-resultados";
        }

        if (!todasRespuestas.isEmpty() && !partida.getFinalizada()) {
            evaluacionServicio.evaluarPartida(partidaId);
            partida = partidaRepositorio.findById(partidaId).orElse(partida);
            todasRespuestas = respuestaRepositorio.findByPartida(partida);
        }

        if (!partida.getFinalizada()) {
            return "redirect:/partida/" + partidaId + "/esperando-resultados";
        }

        todasRespuestas.sort((r1, r2) -> r2.getPuntajeTotal().compareTo(r1.getPuntajeTotal()));

        model.addAttribute("partida", partida);
        model.addAttribute("respuestas", todasRespuestas);
        model.addAttribute("nombreJugador", jugador.getNombre());

        return "resultadosMultijugador";
    }

    @PostMapping("/partida/{partidaId}/volver-sala")
    public String volverASala(@PathVariable Long partidaId, HttpSession session) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null || !partida.getSala().getJugadores().contains(jugador)) {
            return "redirect:/lobby";
        }

        String codigoSala = partida.getSala().getCodigo();
        return "redirect:/lobby/sala/" + codigoSala;
    }

    @PostMapping("/partida/{partidaId}/ir-menu")
    public String irAlMenu(@PathVariable Long partidaId, HttpSession session) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida != null && jugador != null && jugador.getSala() != null) {
            String codigoSala = jugador.getSala().getCodigo();

            Sala sala = salaRepositorio.findById(codigoSala).orElse(null);
            if (sala != null) {
                sala.removerJugador(jugador);

                if (sala.getJugadores().isEmpty()) {
                    salaRepositorio.save(sala);
                } else {
                    if (sala.getCreador().equals(jugador)) {
                        Jugador nuevoCreador = sala.getJugadores().get(0);
                        sala.setCreador(nuevoCreador);
                    }
                    salaRepositorio.save(sala);
                    messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, sala);
                }

                messagingTemplate.convertAndSend("/topic/lobby", salaRepositorio.findAll());
            }
        }

        return "redirect:/menu";
    }
}
