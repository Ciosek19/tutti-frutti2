package com.springboot.obligatorio.tutti_frutti.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Jugador;

@Repository
public interface IJugadorRepositorio extends JpaRepository<Jugador, String>{
    boolean existsByNombre(String nombre);
    Jugador findByNombre(String nombre);
}
