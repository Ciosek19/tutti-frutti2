package com.springboot.obligatorio.tutti_frutti.utilidades;

import java.util.Map;

public final class ConstructorPrompt {
    
    public static String promptValidacionSolitario(String letra, Map<String, String> respuestas) {
    StringBuilder prompt = new StringBuilder();
    
    prompt.append("Eres un juez estricto del juego Tutti Frutti. Valida las siguientes respuestas.\n\n");
    
    prompt.append("=== DATOS DE LA PARTIDA ===\n");
    prompt.append("LETRA: ").append(letra.toUpperCase()).append("\n\n");
    
    prompt.append("RESPUESTAS DEL JUGADOR:\n");
    respuestas.forEach((categoria, respuesta) -> {
        String valorMostrar = (respuesta == null || respuesta.trim().isEmpty()) 
            ? "[vacío]" 
            : respuesta.trim();
        prompt.append("• ").append(categoria).append(": ").append(valorMostrar).append("\n");
    });
    
    prompt.append("\n=== REGLAS DE VALIDACIÓN ===\n");
    prompt.append("1. La respuesta DEBE comenzar con la letra '").append(letra.toUpperCase()).append("'\n");
    prompt.append("2. Debe ser un ejemplo real y válido de la categoría\n");
    prompt.append("3. No aceptar marcas comerciales en categorías genéricas\n");
    prompt.append("4. Respuestas vacías = inválidas (0 puntos)\n");
    prompt.append("5. Ignorar errores ortográficos menores\n\n");
    
    prompt.append("=== PUNTUACIÓN ===\n");
    prompt.append("• Válida: 10 puntos\n");
    prompt.append("• Inválida: 0 puntos\n\n");
    
    prompt.append("=== FORMATO DE RESPUESTA REQUERIDO ===\n");
    prompt.append("Responde con un objeto JSON raw (sin escapar, sin markdown, sin explicaciones).\n");
    prompt.append("NO uses bloques de código.\n");
    prompt.append("NO escapes el JSON con comillas o \\n.\n");
    prompt.append("Devuelve el JSON directamente como si fuera un objeto JavaScript.\n\n");
    
    prompt.append("Estructura exacta:\n");
    prompt.append("{\n");
    prompt.append("  \"validaciones\": [\n");
    prompt.append("    {\n");
    prompt.append("      \"categoria\": \"string\",\n");
    prompt.append("      \"respuestaUsuario\": \"string\",\n");
    prompt.append("      \"esValida\": true,\n");
    prompt.append("      \"puntos\": 10,\n");
    prompt.append("      \"razon\": \"\"\n");
    prompt.append("    }\n");
    prompt.append("  ],\n");
    prompt.append("  \"puntajeTotal\": 0,\n");
    prompt.append("  \"respuestasValidas\": 0,\n");
    prompt.append("  \"respuestasInvalidas\": 0\n");
    prompt.append("}\n\n");
    
    prompt.append("REGLAS ESTRICTAS:\n");
    prompt.append("1. El campo 'razon' debe estar vacío (\"\") si esValida es true\n");
    prompt.append("2. El campo 'razon' debe explicar por que no es valida y tener máximo 20 palabras si esValida es false\n");
    prompt.append("3. NO agregues texto antes o después del JSON\n");
    prompt.append("4. NO uses comillas al inicio y final del JSON completo\n");
    prompt.append("5. NO uses caracteres de escape como \\n, \\t, \\\"\n");
    prompt.append("6. El JSON debe ser parseable directamente\n\n");
    
    prompt.append("Ejemplo de respuesta correcta (copiar este formato exacto):\n");
    prompt.append("{\n");
    prompt.append("  \"validaciones\": [\n");
    prompt.append("    {\"categoria\": \"Animal\", \"respuestaUsuario\": \"Mono\", \"esValida\": true, \"puntos\": 10, \"razon\": \"\"},\n");
    prompt.append("    {\"categoria\": \"Fruta\", \"respuestaUsuario\": \"Perro\", \"esValida\": false, \"puntos\": 0, \"razon\": \"Perro no es una fruta, es un animal\"}\n");
    prompt.append("  ],\n");
    prompt.append("  \"puntajeTotal\": 10,\n");
    prompt.append("  \"respuestasValidas\": 1,\n");
    prompt.append("  \"respuestasInvalidas\": 1\n");
    prompt.append("}\n\n");
    
    prompt.append("Responde AHORA con el JSON:");

    return prompt.toString();
}

    public static String promptValidacionMultijugador(String letra, Map<String, String> respuestas, String nombreJugador) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Eres un juez estricto del juego Tutti Frutti. Valida las siguientes respuestas del jugador en modo multijugador.\n\n");

        prompt.append("=== DATOS DE LA PARTIDA ===\n");
        prompt.append("JUGADOR: ").append(nombreJugador).append("\n");
        prompt.append("LETRA: ").append(letra.toUpperCase()).append("\n\n");

        prompt.append("RESPUESTAS DEL JUGADOR:\n");
        respuestas.forEach((categoria, respuesta) -> {
            String valorMostrar = (respuesta == null || respuesta.trim().isEmpty())
                ? "[vacío]"
                : respuesta.trim();
            prompt.append("• ").append(categoria).append(": ").append(valorMostrar).append("\n");
        });

        prompt.append("\n=== REGLAS DE VALIDACIÓN ===\n");
        prompt.append("1. La respuesta DEBE comenzar con la letra '").append(letra.toUpperCase()).append("'\n");
        prompt.append("2. Debe ser un ejemplo real y válido de la categoría\n");
        prompt.append("3. No aceptar marcas comerciales en categorías genéricas\n");
        prompt.append("4. Respuestas vacías = inválidas (0 puntos)\n");
        prompt.append("5. Ignorar errores ortográficos menores\n\n");

        prompt.append("=== PUNTUACIÓN ===\n");
        prompt.append("• Válida: 10 puntos\n");
        prompt.append("• Inválida: 0 puntos\n\n");

        prompt.append("=== FORMATO DE RESPUESTA REQUERIDO ===\n");
        prompt.append("Responde con un objeto JSON raw (sin escapar, sin markdown, sin explicaciones).\n");
        prompt.append("NO uses bloques de código.\n");
        prompt.append("NO escapes el JSON con comillas o \\n.\n");
        prompt.append("Devuelve el JSON directamente como si fuera un objeto JavaScript.\n\n");

        prompt.append("Estructura exacta:\n");
        prompt.append("{\n");
        prompt.append("  \"validaciones\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"categoria\": \"string\",\n");
        prompt.append("      \"respuestaUsuario\": \"string\",\n");
        prompt.append("      \"esValida\": true,\n");
        prompt.append("      \"puntos\": 10,\n");
        prompt.append("      \"razon\": \"\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"puntajeTotal\": 0,\n");
        prompt.append("  \"respuestasValidas\": 0,\n");
        prompt.append("  \"respuestasInvalidas\": 0\n");
        prompt.append("}\n\n");

        prompt.append("REGLAS ESTRICTAS:\n");
        prompt.append("1. El campo 'razon' debe estar vacío (\"\") si esValida es true\n");
        prompt.append("2. El campo 'razon' debe explicar por qué no es válida y tener máximo 20 palabras si esValida es false\n");
        prompt.append("3. NO agregues texto antes o después del JSON\n");
        prompt.append("4. NO uses comillas al inicio y final del JSON completo\n");
        prompt.append("5. NO uses caracteres de escape como \\n, \\t, \\\"\n");
        prompt.append("6. El JSON debe ser parseable directamente\n\n");

        prompt.append("Ejemplo de respuesta correcta (copiar este formato exacto):\n");
        prompt.append("{\n");
        prompt.append("  \"validaciones\": [\n");
        prompt.append("    {\"categoria\": \"Animal\", \"respuestaUsuario\": \"Mono\", \"esValida\": true, \"puntos\": 10, \"razon\": \"\"},\n");
        prompt.append("    {\"categoria\": \"Fruta\", \"respuestaUsuario\": \"Perro\", \"esValida\": false, \"puntos\": 0, \"razon\": \"Perro no es una fruta, es un animal\"}\n");
        prompt.append("  ],\n");
        prompt.append("  \"puntajeTotal\": 10,\n");
        prompt.append("  \"respuestasValidas\": 1,\n");
        prompt.append("  \"respuestasInvalidas\": 1\n");
        prompt.append("}\n\n");

        prompt.append("Responde AHORA con el JSON:");

        return prompt.toString();
    }

}
