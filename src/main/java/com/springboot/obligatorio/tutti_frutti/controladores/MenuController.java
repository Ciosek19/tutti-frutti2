package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/menu")
public class MenuController {
    @GetMapping("/")
    public String menu() {
        return "menu_principal";
    }
    
}
