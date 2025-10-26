package com.laboratorio.chutes.llm.config;

public record ChutesLlmOptions(
        String model,
        Integer maxTokens,
        Double temperature,
        Integer n
) {
}