package com.laboratorio.iamodelinterface.config;

public record ConnectionData(
        String username,
        String password,
        String url
) {
}