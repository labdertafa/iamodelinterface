package com.laboratorio.chutes.embedding.model;

import java.util.List;

public record ChutesEmbeddingData(
        int index,
        String object,
        List<Float> embedding
) {
}