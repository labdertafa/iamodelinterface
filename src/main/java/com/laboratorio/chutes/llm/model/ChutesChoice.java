package com.laboratorio.chutes.llm.model;

public record ChutesChoice(
        int index,
        ChutesMessage message,
        String finish_reason,
        int matched_stop
) {
}