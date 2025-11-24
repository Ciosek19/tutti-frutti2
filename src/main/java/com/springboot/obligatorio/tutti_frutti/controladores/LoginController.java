package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.repositorios.IJugadorRepositorio;

import jakarta.servlet.http.HttpSession;


@Controller
public class LoginController {

    @Autowired
    private IJugadorRepositorio jugadorRepositorio;

    @GetMapping("")
    public String mostrarRegistro(HttpSession session) {
        if (session.getAttribute("jugador") != null) {
            return "redirect:/menu";
        }
        return "login";
    }

    @PostMapping("")
    public String registro(@RequestParam String nombre, HttpSession session) {
        if (nombre.trim().isEmpty()) {
            return "redirect:/";
        }
        try {
            Jugador jugador = new Jugador(nombre);
            jugadorRepositorio.save(jugador);
            session.setAttribute("nombreJugador", nombre);
        } catch (Exception e) {
            return "redirect:/";
        }
        return "redirect:/menu";
    }
}
