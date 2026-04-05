package com.laboratorio.iamodelinterface.util;

public enum IdiomaEnum {
    FRANCES("fr-FR"),
    CASTELLANO("es-ES"),
    INGLES("en-US");

    private String idioma;

    IdiomaEnum(String idioma) {
        this.idioma = idioma;
    }

    public String getValue() {
        return idioma;
    }
}