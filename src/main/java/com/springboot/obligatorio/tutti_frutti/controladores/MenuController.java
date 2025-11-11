package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    
}
