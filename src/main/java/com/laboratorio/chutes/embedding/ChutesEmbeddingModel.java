package com.laboratorio.chutes.embedding;

import com.laboratorio.chutes.embedding.config.ChutesEmbeddingApi;
import com.laboratorio.chutes.embedding.model.ChutesEmbeddingRequest;
import com.laboratorio.chutes.embedding.model.ChutesEmbeddingResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.*;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChutesEmbeddingModel implements EmbeddingModel {
    private final ChutesEmbeddingApi api;
    private final RestClient restClient;

    public ChutesEmbeddingModel(ChutesEmbeddingApi api) {
        this.api = api;

        this.restClient = RestClient.builder()
                .baseUrl(this.api.baseUrl())
                .defaultHeader("Authorization", "Bearer " + this.api.apiKey())
                .build();
    }

    private float[] getEmbedding(String text) {
        String uri = this.api.baseUrl() + "/" + this.api.uri();
        ChutesEmbeddingResponse response = this.restClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + this.api.apiKey())
                .body(new ChutesEmbeddingRequest(text))
                .retrieve()
                .body(ChutesEmbeddingResponse.class);

        if (response == null) {
            return null;
        }
        if (response.data() == null || response.data().isEmpty()) {
            return null;
        }

        float[] vector = new float[response.data().getFirst().embedding().size()];
        List<Float> vectorList = response.data().getFirst().embedding();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vectorList.get(i);
        }

        return vector;
    }

    @Override
    @NonNull
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<String> texts = request.getInstructions();
        List<Embedding> embeddings = new ArrayList<>();

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            float[] vector = this.getEmbedding(text);
            Objects.requireNonNull(vector);
            Embedding embedding = new Embedding(vector, i);
            embeddings.add(embedding);
        }

        return new EmbeddingResponse(embeddings);
    }

    @Override
    @NonNull
    public float[] embed(Document document) {
        return Objects.requireNonNull(this.getEmbedding(document.getText()));
    }
}