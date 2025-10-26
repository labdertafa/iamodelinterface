package com.laboratorio.chutes.image.config;

public record ChutesImageOptions(
        String model,
        Integer width,
        Integer height,
        Double guidance_scale,
        Integer num_inference_steps
) {
}