package com.springboot.obligatorio.tutti_frutti.controladores;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
import com.springboot.obligatorio.tutti_frutti.servicios.JugadorServicio;
import com.springboot.obligatorio.tutti_frutti.servicios.SalaServicio;

@Controller
public class SalaController {

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private JugadorServicio jugadorServicio;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/actualizar-sala/{codigo}")
    public void actualizarSala(@DestinationVariable String codigo) {
        System.out.println("Actualizando sala: " + codigo);

        Sala sala = salaServicio.buscarPorCodigo(codigo);

        if (sala == null) {
            return;
        }
        messagingTemplate.convertAndSend("/topic/sala/" + codigo, sala);
    }

    @PostMapping("/sala/salir-sala")
    public String salirDeSala(
            @RequestParam String codigoSala,
            HttpSession session) {
        String idJugador = (String) session.getAttribute("idJugador");
        Jugador jugador = jugadorServicio.obtenerJugador(idJugador);
        if (jugador == null) {
            return "redirect:/";
        }

        salaServicio.salirDeSala(codigoSala, jugador.getId());

        Sala sala = salaServicio.buscarPorCodigo(codigoSala);
        if (sala != null) {
            messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, sala);
        }

        List<Sala> salas = salaServicio.obtenerSalas();
        messagingTemplate.convertAndSend("/topic/lobby", salas);

        return "redirect:/lobby";
    }

    @MessageMapping("/configurar-categorias")
    public void configurarCategorias(@Payload Map<String, Object> datos) {
        String codigoSala = (String) datos.get("codigoSala");
        int cantidadCategorias = ((Number) datos.get("cantidadCategorias")).intValue();
        
        Sala sala = salaServicio.buscarPorCodigo(codigoSala);
        sala.setCantidadCategorias(cantidadCategorias);
        Sala salaBD = salaServicio.guardarSala(sala);
        messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, salaBD);
    }

    @MessageMapping("/configurar-max-jugadores")
    public void configurarMaxJugadores(@Payload Map<String, Object> datos) {
        String codigoSala = (String) datos.get("codigoSala");
        int maxJugadores = ((Number) datos.get("maxJugadores")).intValue();

        Sala sala = salaServicio.buscarPorCodigo(codigoSala);
        if (sala.getJugadores().size() > maxJugadores) {
            return;
        }
        sala.setMaxJugadores(maxJugadores);
        Sala salaBD = salaServicio.guardarSala(sala);
        List<Sala> salas = salaServicio.obtenerSalas();
        messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, salaBD);
        messagingTemplate.convertAndSend("/topic/lobby", salas);
    }

    @MessageMapping("/configurar-duracion")
    public void configurarDuracion(@Payload Map<String, Object> datos) {
        String codigoSala = (String) datos.get("codigoSala");
        int duracion = ((Number) datos.get("duracion")).intValue();

        Sala sala = salaServicio.buscarPorCodigo(codigoSala);
        sala.setDuracion(duracion);
        Sala salaBD = salaServicio.guardarSala(sala);
        messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, salaBD);
    }

}
