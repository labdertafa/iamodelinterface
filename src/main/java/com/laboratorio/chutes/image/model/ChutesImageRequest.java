package com.laboratorio.chutes.image.model;

public record ChutesImageRequest(
        String model,
        String prompt,
        int width,
        int height,
        double guidance_scale,
        int num_inference_steps
) {
}