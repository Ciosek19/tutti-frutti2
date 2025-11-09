package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/solitario")
public class SolitarioController {

    @GetMapping("/")
    public String dificultad() {
        return "dificultad";
    }
    
    @PostMapping("/")
    public String generarPartida() {
        // Obtenemos la dificultad seleccionada por parametro
        // Creamos la partida con su configuracion
        // Enviamos la partida hacia el metodo GET iniciarPartida() cargado con "redirectAttributes.addFlashAttribute".
        return "redirect:/";
    }

    @GetMapping("/jugar")
    public String iniciarPartida() {
        // Obtenemos la partida creada en el metodo POST
        // Agregamos el temporizador (HTML + Thymeleaf + JavaScript)
        // La cargamos en el modelo (Model model)
        // Mostramos la vista de la partida con el modelo
        return "partida";
    }

    @PostMapping("/validar")
    public String validarRespuestas() {
        // Obtener las respuestas de la partida por parametro
        // Validar las respuestas con la logica del juego
        // Cargar el resultado en el modelo.
        // Mostrar el resultado de la partida (ganada/perdida) en la vista resultadoSolitario.html
        return "redirect:/solitario/resultado";
    }
    
    
}
