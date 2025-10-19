package com.laboratorio.chutes.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChutesException extends RuntimeException {
    protected static final Logger log = LogManager.getLogger(ChutesException.class);

    public ChutesException(String message) {
        super(message);
        log.error("Mensaje: {}", message);
    }

    public ChutesException(String message, Throwable cause) {
        super(message, cause);
        log.error("Error: {}", message);
        log.error("Detalle: {}", cause.getMessage());
        if (cause.getCause() !=  null) {
            log.error("Causa: {}", cause.getCause().getMessage());
        }
    }
}