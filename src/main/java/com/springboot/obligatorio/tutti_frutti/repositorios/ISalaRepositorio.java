package com.springboot.obligatorio.tutti_frutti.repositorios;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.obligatorio.tutti_frutti.modelos.entidades.Sala;

@Repository
public interface ISalaRepositorio extends JpaRepository<Sala, String>{
}
