package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Partida;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.PartidaSolitario;
import com.springboot.obligatorio.tutti_frutti.modelos.enums.Dificultad;
import com.springboot.obligatorio.tutti_frutti.servicios.ServicioIA;

import jakarta.servlet.http.HttpSession;


@Controller
public class SolitarioController {

    @Autowired
    private ServicioIA servicioIA;

    @GetMapping("/solitario")
    public String dificultad() {
        return "dificultad";
    }
    
    @PostMapping("/solitario/dificultad")
    public String generarPartida(@RequestParam String dificultad, HttpSession session) { // Obtenemos la dificultad seleccionada por parametro

        // Creamos la partida con su configuracion(cantidad de categorias y duracion).
        Jugador jugador = (Jugador)session.getAttribute("jugador");
        Dificultad dif = Dificultad.valueOf(dificultad);
        Partida partida = new PartidaSolitario(jugador, dif, servicioIA);

        // Guardar la partida en la sesion y redirigir al GET /solitario/jugar
        session.setAttribute("partidaActual", partida);

        return "redirect:/solitario/jugar";
    }

    @GetMapping("/solitario/jugar")
    public String iniciarPartida(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        // Obtenemos la partida creada en el metodo POST
        Partida partida = (Partida)session.getAttribute("partidaActual");
        // Inicializamos las categorias y la letra aleatorias
        partida.asignarCategoriasAleatorias();
        partida.asignarLetraAleatoria();

        // La cargamos en el modelo (Model model)
        model.addAttribute("partida", partida);
        
        // Agregamos el temporizador (HTML + Thymeleaf + JavaScript)
        // Mostramos la vista de la partida con los datos del modelo
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
