package com.laboratorio.iamodelinterface.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class SupabaseStorageService {
    @Value("${supabase.storage.base.url}")
    private String supabaseUrl;
    @Value("${supabase.storage.api.key}")
    private String apiKey;
    @Value("${supabase.storage.endpoint}")
    private String endpoint;
    @Value("${supabase.storage.bucket.name}")
    private String bucket;

    private WebClient webClient;

    @PostConstruct
    public void setUp() {
        this.webClient = WebClient.builder()
                .baseUrl(this.supabaseUrl)
                .defaultHeader("apikey", this.apiKey)
                .defaultHeader("Authorization", "Bearer " + this.apiKey)
                .build();
    }

    public byte[] getImagen(String fileName) {
        String uri = String.format("%s/%s/%s", this.endpoint, this.bucket, fileName);

        try {
            return this.webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            log.error("Error al obtener la imagen de Supabase: {}", e.getMessage());
            return null;
        }
    }
}