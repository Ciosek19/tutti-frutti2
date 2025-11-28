package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.servicios.JugadorServicio;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private JugadorServicio jugadorServicio;

    @GetMapping("")
    public String mostrarRegistro(HttpSession session, Model model) {
        session.invalidate();
        return "login";
    }

    @PostMapping("")
    public String registro(@RequestParam String nombre, HttpSession session, RedirectAttributes redirectAttributes) {
        if (nombre.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre no peude estar vacio.");
            return "redirect:/";
        }

        if (jugadorServicio.existeNombre(nombre)) {
            redirectAttributes.addFlashAttribute("error", "Ese nombre ya está en uso. Por favor, elige otro.");
            return "redirect:/";
        }

        Jugador jugador = jugadorServicio.crearJugador(nombre);
        session.setAttribute("idJugador", jugador.getId());

        return "redirect:/menu";
    }
}
