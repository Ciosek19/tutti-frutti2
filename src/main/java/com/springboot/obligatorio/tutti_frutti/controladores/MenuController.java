package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.springboot.obligatorio.tutti_frutti.repositorios.IJugadorRepositorio;

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
