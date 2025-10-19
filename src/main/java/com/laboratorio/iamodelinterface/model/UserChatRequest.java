package com.laboratorio.iamodelinterface.model;

public record UserChatRequest(
        String userId,
        String message
) {
}