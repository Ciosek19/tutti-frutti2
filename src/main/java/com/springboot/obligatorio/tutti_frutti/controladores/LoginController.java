package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/")
    public String login(@RequestParam String nombre) {
        // 1. Validar nombre de usuario
        // 2. Si no es valido, redirigir de nuevo a la pagina de login
        // 3. Si es valido, redirigir al menu principal
        return "redirect:/menu";
    }
}
