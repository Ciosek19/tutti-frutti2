package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import jakarta.servlet.http.HttpSession;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String menu() {
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
