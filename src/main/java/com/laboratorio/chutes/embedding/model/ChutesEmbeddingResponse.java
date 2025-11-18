package com.laboratorio.chutes.embedding.model;

import java.util.List;

public record ChutesEmbeddingResponse(
        String id,
        String object,
        String created,
        String model,
        List<ChutesEmbeddingData> data,
        ChutesEmbeddingUsage usage
) {
}