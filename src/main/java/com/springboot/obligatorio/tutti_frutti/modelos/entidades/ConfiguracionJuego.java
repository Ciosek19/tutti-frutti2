package com.springboot.obligatorio.tutti_frutti.modelos.entidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class ConfiguracionJuego {
    public static final List<String> CATEGORIAS = List.of(
        "Nombres", "Países", "Ciudades", "Animales", 
        "Frutas", "Verduras", "Colores", "Profesiones", "Marcas",
        "Películas", "Deportes", "Objetos", "Comidas", "Bebidas",
        "Instrumentos", "Flores", "Programas de TV", "Cantantes",
        "Villanos", "Superhéroes", "Videojuegos", "Ropa", 
        "Muebles", "Partes del cuerpo", "Herramientas", "Juguetes",
        "Electrodomésticos", "Transportes", "Idiomas", "Planetas"
    );
    
    public static final List<Character> LETRAS = List.of(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        'L', 'M', 'N', 'O', 'P', 'R', 'S', 'T', 'U', 'V'
    );
    
    public static String getLetraAleatoria() {
        Random random = new Random();
        return String.valueOf(LETRAS.get(random.nextInt(LETRAS.size())));
    }
    
    public static List<String> getCategoriasAleatorias(int cantidad) {
        List<String> copia = new ArrayList<>(CATEGORIAS);
        Collections.shuffle(copia);
        return copia.subList(0, Math.min(cantidad, copia.size()));
    }
    
    private ConfiguracionJuego() {}
}
