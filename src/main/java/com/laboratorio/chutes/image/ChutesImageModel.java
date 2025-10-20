package com.laboratorio.chutes.image;

import com.laboratorio.chutes.exception.ChutesException;
import com.laboratorio.chutes.image.config.ChutesImageApi;
import com.laboratorio.chutes.image.config.ChutesImageOptions;
import com.laboratorio.chutes.image.model.ChutesImageRequest;
import org.springframework.ai.image.*;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;

public class ChutesImageModel implements ImageModel {
    private final ChutesImageApi imageApi;
    private final ChutesImageOptions imageOptions;
    private final RestClient restClient;

    public ChutesImageModel(ChutesImageApi imageApi, ChutesImageOptions imageOptions) {
        this.imageApi = imageApi;
        this.imageOptions = imageOptions;
        this.restClient = RestClient.builder()
                .baseUrl(this.imageApi.baseUrl())
                .defaultHeader("Authorization", "Bearer " + this.imageApi.apiKey())
                .build();
    }

    @Override
    @NonNull
    public ImageResponse call(ImagePrompt imagePrompt) {
        try {
            if (imagePrompt.getInstructions().isEmpty()) {
                throw new ChutesException("No se puede construir la imagen con una solicitud vacía");
            }

            // 1. Convertir el ImagePrompt de Spring AI al formato que espera Chutes
            ChutesImageRequest request = this.createRequestFromImagePrompt(imagePrompt);

            // 2. Llamar a la API
            ResponseEntity<byte[]> response = this.restClient.post()
                    .uri(this.imageApi.uri())
                    .body(request)
                    .retrieve()
                    .toEntity(byte[].class);

            // 3. Convertir la respuesta de Chutes al ImageResponse de Spring AI
            return this.convertResponseToImageResponse(response);
        } catch (Exception e) {
            throw new ChutesException("Ocurrió un error al llamar a Chutes AI", e);
        }
    }

    private ChutesImageRequest createRequestFromImagePrompt(ImagePrompt imagePrompt) {
        String prompt = imagePrompt.getInstructions().getFirst().getText();

        return new ChutesImageRequest(this.imageOptions.model(), prompt, this.imageOptions.width(), this.imageOptions.height(),
                this.imageOptions.guidance_scale(), this.imageOptions.num_inference_steps());
    }

    private ImageResponse convertResponseToImageResponse(ResponseEntity<byte[]> response) {
        // Obtener los bytes de la imagen
        byte[] imageBytes = response.getBody();
        if (imageBytes == null || imageBytes.length == 0) {
            throw new ChutesException( "No hubo una respuesta válida de Chutes  AI");
        }

        // Convertir a Base64
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Crear objetos de Spring AI
        Image image = new Image(null, base64Image);
        ImageGeneration generation = new ImageGeneration(image);

        return new ImageResponse(List.of(generation));
    }
}