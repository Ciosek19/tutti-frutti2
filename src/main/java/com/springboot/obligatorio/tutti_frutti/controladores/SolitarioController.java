package com.springboot.obligatorio.tutti_frutti.controladores;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.obligatorio.tutti_frutti.modelos.dtos.ValidacionesDTO;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.PartidaSolitario;
import com.springboot.obligatorio.tutti_frutti.modelos.enums.Dificultad;
import com.springboot.obligatorio.tutti_frutti.servicios.IAServicio;
import com.springboot.obligatorio.tutti_frutti.servicios.JugadorServicio;

import jakarta.servlet.http.HttpSession;


@Controller
public class SolitarioController {

    @Autowired
    private IAServicio servicioIA;

    @Autowired
    private JugadorServicio jugadorServicio;

    @GetMapping("/solitario")
    public String dificultad(HttpSession session, Model model) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "login";
        }
        String nombreJugador = jugador.getNombre();

        model.addAttribute("nombreJugador", nombreJugador);
        return "dificultad";
    }

    @PostMapping("/solitario/dificultad")
    public String generarPartida(@RequestParam String dificultad, HttpSession session) {
        Jugador jugador = (Jugador) session.getAttribute("jugador");

        Dificultad dif = Dificultad.valueOf(dificultad);
        PartidaSolitario partida = new PartidaSolitario(jugador, dif);

        session.setAttribute("partidaActual", partida);

        return "redirect:/solitario/jugar";
    }

    @GetMapping("/solitario/jugar")
    public String iniciarPartida(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        PartidaSolitario partida = (PartidaSolitario)session.getAttribute("partidaActual");
        model.addAttribute("partida", partida);
        
        return "partidaSolitario";
    }

    @PostMapping("solitario/validar")
    public String validarRespuestas(@RequestParam Map<String,String> respuestas, String letra, HttpSession session, Model model) {
        respuestas.remove("letra");
        ValidacionesDTO resultado = servicioIA.validarRespuestas(respuestas, letra);

        // Obtener el nombre del jugador de la sesión
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        String nombreJugador = jugador != null ? jugador.getNombre() : "Jugador";

        model.addAttribute("resultado", resultado);
        model.addAttribute("nombreJugador", nombreJugador);
        model.addAttribute("letra", letra);
        return "solitarioResultados";
    }

    
}
