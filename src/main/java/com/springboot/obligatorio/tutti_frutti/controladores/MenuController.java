package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.servicios.JugadorServicio;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuController {

    @Autowired
    private JugadorServicio jugadorServicio;

    @GetMapping("/menu")
    public String menu(HttpSession session, Model model) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "login";
        }
        String nombreJugador = jugador.getNombre();

        model.addAttribute("nombreJugador", nombreJugador);
        return "menu_principal";
    }

    @PostMapping("/menu/solitario")
    public String solitario() {
        return "redirect:/solitario";
    }

    @PostMapping("/menu/multijugador")
    public String multijugador(HttpSession session) {
        return "redirect:/lobby";
    }

}
