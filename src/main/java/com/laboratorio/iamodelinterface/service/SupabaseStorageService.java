package com.laboratorio.iamodelinterface.service;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class SupabaseStorageService {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");
    private WebClient webClient;

    @PostConstruct
    public void setUp() {
        String supabaseUrl = this.config.getProperty("supabase.storage.base.url");
        String apiKey =  this.config.getProperty("supabase.storage.api.key");

        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey", apiKey)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public byte[] getImagen(String fileName) {
        String endpoint = this.config.getProperty("supabase.storage.endpoint");
        String bucket = this.config.getProperty("supabase.storage.bucket.name");
        String uri = String.format("%s/%s/%s", endpoint, bucket, fileName);

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