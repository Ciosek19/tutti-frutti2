package com.springboot.obligatorio.tutti_frutti.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;
import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;
import com.springboot.obligatorio.tutti_frutti.repositorios.IJugadorRepositorio;
import com.springboot.obligatorio.tutti_frutti.servicios.SalaServicio;

@Controller
public class SalaController {

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private IJugadorRepositorio jugadorRepositorio;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/actualizar-sala/{codigo}")
    public void actualizarSala(@DestinationVariable String codigo) {
        System.out.println("Actualizando sala: " + codigo);

        // Buscar la sala
        Sala sala = salaServicio.buscarPorCodigo(codigo);

        if (sala == null) {
            System.err.println("Sala no encontrada: " + codigo);
            return;
        }
        // Enviar manualmente al topic específico
        messagingTemplate.convertAndSend("/topic/sala/" + codigo, sala);
        System.out.println("Sala enviada a /topic/sala/" + codigo);
    }

    @PostMapping("/sala/salir-sala")
    public String salirDeSala(
            @RequestParam String codigoSala,
            HttpSession session) {

        String nombreJugador = (String)session.getAttribute("nombreJugador");
        System.out.println("SalaController.salirDeSala() -> nombreJugador: "+nombreJugador);
        if (nombreJugador == null) {
            return "redirect:/";
        }
        System.out.println("SalaController.salirDeSala() -> valor codigoSala = "+codigoSala);
        salaServicio.salirDeSala(codigoSala, nombreJugador);

        Jugador jugadorActualizado = jugadorRepositorio.findById(nombreJugador)
                .orElse(new Jugador(nombreJugador));
        jugadorRepositorio.save(jugadorActualizado);

        session.setAttribute("jugador", jugadorActualizado);

        Sala sala = salaServicio.buscarPorCodigo(codigoSala);
        if (sala != null) {
            messagingTemplate.convertAndSend("/topic/sala/" + codigoSala, sala);
        }
        return "redirect:/lobby";
    }

}
