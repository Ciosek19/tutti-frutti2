package com.springboot.obligatorio.tutti_frutti.controladores;

import java.util.List;
import java.util.Optional;

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
import jakarta.transaction.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
import com.springboot.obligatorio.tutti_frutti.repositorios.IJugadorRepositorio;
import com.springboot.obligatorio.tutti_frutti.servicios.SalaServicio;

import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LobbyController {

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private IJugadorRepositorio jugadorRepositorio;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 1. Obtener salas
    @MessageMapping("/obtener-salas")
    @SendTo("/topic/lobby")
    @Transactional
    public List<Sala> obtenerSalas() {
        System.out.println("obtenerSalas() -> Obteniendo salas desde BD");
        List<Sala> salas = salaServicio.obtenerSalas();
        System.out.println("obtenerSalas() -> Devolviendo " + salas.size() + " salas");
        return salas;
    }
    
    @GetMapping("/lobby")
    public String lobby(HttpSession session, Model model) {
        String nombreJugador = (String)session.getAttribute("nombreJugador");
        model.addAttribute("nombreJugador", nombreJugador);
        Jugador jugador = jugadorRepositorio.findById(nombreJugador).orElse(null);
        if (jugador == null) {
            return "redirect:/";
        }
        return "lobby";
    }

    // 2. Crear salas
    @PostMapping("/sala/crear-sala")
    public String crearSala(@RequestParam String nombre, RedirectAttributes redirectAttributes, HttpSession session) {
        String creador = (String)session.getAttribute("nombreJugador");

        Sala sala = salaServicio.crearSala(nombre, creador);
        messagingTemplate.convertAndSend("/topic/lobby", sala);
        
        redirectAttributes.addFlashAttribute("sala", sala);
        redirectAttributes.addFlashAttribute("nombreJugador", creador);

        return unirseSala(sala.getCodigo(),session,redirectAttributes);
    }
 
    // 3. Unirse a sala
    @PostMapping("/sala/{codigo}")
    public String unirseSala(
            @PathVariable String codigo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String nombreJugador = (String)session.getAttribute("nombreJugador");

        salaServicio.unirseASala(codigo, nombreJugador);

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
        redirectAttributes.addFlashAttribute("nombreJugador", nombreJugador);
        return "redirect:/lobby/sala/" + codigo;
    }

    @GetMapping("/lobby/sala/{codigo}")
    public String unirseSala(@PathVariable String codigo, Model model) {
        return "sala";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}