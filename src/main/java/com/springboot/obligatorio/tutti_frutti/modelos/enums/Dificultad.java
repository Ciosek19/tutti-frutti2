package com.springboot.obligatorio.tutti_frutti.modelos.enums;

public enum Dificultad {
    FACIL(5, 60),
    MEDIO(7, 50),
    DIFICIL(10, 40);

    private final int cantidadCategorias;
    private final int segundos;

    Dificultad(int categorias, int segundos) {
        this.cantidadCategorias = categorias;
        this.segundos = segundos;
    }

    public int getCantidadCategorias() { return cantidadCategorias; }
    public int getSegundos() { return segundos; }
}
