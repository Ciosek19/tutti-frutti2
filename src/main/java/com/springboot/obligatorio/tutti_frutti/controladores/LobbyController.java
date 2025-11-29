package com.springboot.obligatorio.tutti_frutti.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
import com.springboot.obligatorio.tutti_frutti.repositorios.ISalaRepositorio;
import com.springboot.obligatorio.tutti_frutti.servicios.JugadorServicio;
import com.springboot.obligatorio.tutti_frutti.servicios.SalaServicio;

import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LobbyController {

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private JugadorServicio jugadorServicio;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ISalaRepositorio salaRepositorio;

    @MessageMapping("/obtener-salas")
    @SendTo("/topic/lobby")
    public List<Sala> obtenerSalas() {
        List<Sala> salas = salaServicio.obtenerSalas();
        return salas;
    }

    @GetMapping("/lobby")
    public String lobby(HttpSession session, Model model) {
        String idJugador = (String) session.getAttribute("idJugador");
        try {
            Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
            model.addAttribute("nombreJugador", jugador.getNombre());
        } catch (Exception e) {
            return "redirect:/";
        }
        return "lobby";
    }

    @PostMapping("/sala/crear-sala")
    public String crearSala(@RequestParam String nombre, RedirectAttributes redirectAttributes, HttpSession session) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "login";
        }
        Sala sala = salaServicio.crearSala(nombre, jugador);
        List<Sala> salas = salaServicio.obtenerSalas();
        messagingTemplate.convertAndSend("/topic/lobby", salas);

        messagingTemplate.convertAndSend("/topic/sala/" + sala.getCodigo(), sala);

        redirectAttributes.addFlashAttribute("sala", sala);
        redirectAttributes.addFlashAttribute("nombreJugador", jugador.getNombre());

        return "redirect:/lobby/sala/" + sala.getCodigo();
    }

    @PostMapping("/sala/{codigo}")
    public String unirseSala(
            @PathVariable String codigo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }
        salaServicio.unirseASala(codigo, jugador.getId());

        List<Sala> salas = salaServicio.obtenerSalas();
        messagingTemplate.convertAndSend("/topic/lobby", salas);

        Sala sala = salaServicio.buscarPorCodigo(codigo);
        if (sala != null) {
            messagingTemplate.convertAndSend("/topic/sala/" + codigo, sala);
        }

        if (sala == null) {
            return "redirect:/lobby";
        }

        redirectAttributes.addFlashAttribute("sala", sala);
        redirectAttributes.addFlashAttribute("nombreJugador", jugador.getNombre());
        return "redirect:/lobby/sala/" + codigo;
    }

    @GetMapping("/lobby/sala/{codigo}")
    public String mostrarSala(@PathVariable String codigo, Model model, HttpSession session) {
        String idJugador = (String) session.getAttribute("idJugador");
        try {
            Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
            model.addAttribute("nombreJugador", jugador.getNombre());
        } catch (Exception e) {
            return "redirect:/";
        }

        try {
            Sala sala = salaServicio.buscarPorCodigo(codigo);
            model.addAttribute("sala", sala);
        } catch (Exception e) {
            return "redirect:/lobby";
        }
        return "sala";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);

        if (jugador != null) {
            if (jugador.getSala() != null) {
                String codigoSala = jugador.getSala().getCodigo();
                salaServicio.salirDeSala(codigoSala, idJugador);

                Sala sala = salaServicio.buscarPorCodigo(codigoSala);
                if (sala != null) {
                    messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, sala);
                }
            }

            List<Sala> salasComoCreador = salaRepositorio.findByCreador(jugador);
            for (Sala sala : salasComoCreador) {
                salaRepositorio.delete(sala);
            }

            List<Sala> salas = salaServicio.obtenerSalas();
            messagingTemplate.convertAndSend("/topic/lobby", salas);

            jugadorServicio.eliminarJugador(idJugador);
        }

        session.invalidate();
        return "redirect:/";
    }
}