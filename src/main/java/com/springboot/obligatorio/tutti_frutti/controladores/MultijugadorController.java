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
public class MultijugadorController {

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

        System.out.println("=== GUARDANDO RESPUESTAS ===");
        System.out.println("Jugador: " + jugador.getNombre());
        System.out.println("Partida ID: " + partidaId);
        System.out.println("Respuestas recibidas:");
        respuestasMap.forEach((k, v) -> System.out.println("  - " + k + ": " + v));

        Respuesta respuesta = respuestaRepositorio.findByPartidaAndJugador(partida, jugador)
                .orElse(null);

        if (respuesta != null) {
            System.out.println("Respuesta existente encontrada, eliminando...");
            respuestaRepositorio.delete(respuesta);
            respuestaRepositorio.flush();
        }

        respuesta = new Respuesta(jugador, partida);

        for (Map.Entry<String, String> entry : respuestasMap.entrySet()) {
            String categoria = entry.getKey();
            String respuestaTexto = entry.getValue();
            RespuestaIndividual respuestaInd = new RespuestaIndividual(categoria, respuestaTexto);
            respuesta.agregarRespuestaIndividual(respuestaInd);
            System.out.println("Agregando respuesta individual: " + categoria + " -> " + respuestaTexto);
        }

        Respuesta respuestaGuardada = respuestaRepositorio.save(respuesta);
        System.out.println("Respuesta guardada en BD con ID: " + respuestaGuardada.getId());
        System.out.println("Total respuestas individuales guardadas: " + respuestaGuardada.getRespuestasIndividuales().size());

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);
        int totalJugadores = partida.getSala().getJugadores().size();

        System.out.println("Respuestas recibidas: " + todasRespuestas.size() + "/" + totalJugadores);

        if (todasRespuestas.size() >= totalJugadores && !partida.getFinalizada()) {
            System.out.println("Todas las respuestas recibidas, iniciando evaluación...");
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

        if (partida == null || !partida.getSala().getJugadores().contains(jugador)) {
            return "redirect:/lobby";
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);
        int totalJugadores = partida.getSala().getJugadores().size();

        if (partida.getFinalizada() || todasRespuestas.size() >= totalJugadores) {
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

    @GetMapping("/partida/{partidaId}/debug")
    @ResponseBody
    public Map<String, Object> debugPartida(@PathVariable Long partidaId) {
        PartidaMultijugador partida = partidaRepositorio.findById(partidaId).orElse(null);

        if (partida == null) {
            return Map.of("error", "Partida no encontrada");
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);

        System.out.println("\n=== DEBUG PARTIDA " + partidaId + " ===");
        System.out.println("Letra: " + partida.getLetra());
        System.out.println("Finalizada: " + partida.getFinalizada());
        System.out.println("Total respuestas: " + todasRespuestas.size());

        for (Respuesta resp : todasRespuestas) {
            System.out.println("\n- Jugador: " + resp.getJugador().getNombre());
            System.out.println("  ID Respuesta: " + resp.getId());
            System.out.println("  Validada: " + resp.getValidada());
            System.out.println("  Puntaje: " + resp.getPuntajeTotal());
            System.out.println("  Respuestas individuales: " + resp.getRespuestasIndividuales().size());

            for (RespuestaIndividual ri : resp.getRespuestasIndividuales()) {
                System.out.println("    * " + ri.getCategoria() + ": " + ri.getRespuesta());
                System.out.println("      Válida: " + ri.getEsValida() + ", Puntos: " + ri.getPuntos());
                if (ri.getRazon() != null && !ri.getRazon().isEmpty()) {
                    System.out.println("      Razón: " + ri.getRazon());
                }
            }
        }

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

        if (partida == null || !partida.getSala().getJugadores().contains(jugador)) {
            return "redirect:/lobby";
        }

        List<Respuesta> todasRespuestas = respuestaRepositorio.findByPartida(partida);
        int totalJugadores = partida.getSala().getJugadores().size();

        if (todasRespuestas.size() >= totalJugadores && !partida.getFinalizada()) {
            System.out.println("Jugador llegó a resultados antes de evaluación, iniciando evaluación...");
            evaluacionServicio.evaluarPartida(partidaId);
            partida = partidaRepositorio.findById(partidaId).orElse(partida);
            todasRespuestas = respuestaRepositorio.findByPartida(partida);
        }

        if (!partida.getFinalizada() || todasRespuestas.isEmpty()) {
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
                    System.out.println("Sala " + codigoSala + " quedó vacía");
                    salaRepositorio.save(sala);
                } else {
                    if (sala.getCreador().equals(jugador)) {
                        Jugador nuevoCreador = sala.getJugadores().get(0);
                        sala.setCreador(nuevoCreador);
                        System.out.println("Nuevo creador de sala: " + nuevoCreador.getNombre());
                    }
                    salaRepositorio.save(sala);
                    messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, sala);
                }

                messagingTemplate.convertAndSend("/topic/lobby", salaRepositorio.findAll());
                System.out.println("Jugador " + jugador.getNombre() + " salió de sala " + codigoSala + " hacia menú");
            }
        }

        return "redirect:/menu";
    }
}
