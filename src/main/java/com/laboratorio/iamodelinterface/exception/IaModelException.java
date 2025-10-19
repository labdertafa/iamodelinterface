package com.laboratorio.iamodelinterface.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IaModelException extends RuntimeException {
    protected static final Logger log = LogManager.getLogger(IaModelException.class);

    public IaModelException(String message) {
        super(message);
        log.error("Mensaje: {}", message);
    }

    public IaModelException(String message, Throwable cause) {
        super(message, cause);
        log.error("Error: {}", message);
        log.error("Detalle: {}", cause.getMessage());
        if (cause.getCause() !=  null) {
            log.error("Causa: {}", cause.getCause().getMessage());
        }
    }
}