package com.laboratorio.chutes.llm.model;

public record ChutesUsage(
        String prompt_tokens,
        String total_tokens,
        String completion_tokens
) {
}