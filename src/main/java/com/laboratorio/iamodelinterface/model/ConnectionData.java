package com.laboratorio.iamodelinterface.model;

public record ConnectionData(
        String username,
        String password,
        String url
) {
}