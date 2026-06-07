package com.laboratorio.iamodelinterface.model;

public enum IAProvider {
    CHUTES, GEMINI, GROQ, ROUTER;

    public static IAProvider from(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuración: nombre de IAProvider no debe ser nulo ni vacío");
        }

        String normalized = name.trim();
        for (IAProvider p : IAProvider.values()) {
            if (p.name().equalsIgnoreCase(normalized)) {
                return p;
            }
        }

        throw new IllegalArgumentException("Configuración IAProvider desconocido: '" + name + "'");
    }
}