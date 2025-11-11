package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;

import jakarta.servlet.http.HttpSession;


@Controller
public class LoginController {

    @GetMapping("")
    public String login() {
        return "login";
    }

    @PostMapping("")
    public String login(@RequestParam String nombre, HttpSession session) {
        // 1. Validar nombre de usuario
        if (nombre == null || nombre.trim().isEmpty()) {
            // 2. Si no es valido, redirigir de nuevo a la pagina de login
            return "redirect:/";
        }
        String uuid = java.util.UUID.randomUUID().toString();
        // 3. Si es valido, guardarlo en la session y redirigir al menu principal
        Jugador jugador = new Jugador(nombre,uuid);
        session.setAttribute("jugador", jugador);
        return "redirect:/menu";
    }
}
