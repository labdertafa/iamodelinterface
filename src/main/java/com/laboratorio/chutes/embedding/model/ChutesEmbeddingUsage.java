package com.laboratorio.chutes.embedding.model;

public record ChutesEmbeddingUsage(
        int prompt_tokens,
        int total_tokens,
        int completion_tokens
) {
}