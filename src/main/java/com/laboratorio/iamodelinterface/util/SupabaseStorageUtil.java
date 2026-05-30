package com.laboratorio.iamodelinterface.util;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class SupabaseStorageUtil {
    private final ReaderConfig config;
    private final String eventPrefix;
    private final WebClient webClient;

    public SupabaseStorageUtil(String eventName) {
        this.config = new ReaderConfig("config//ia_models_config.properties");

        this.eventPrefix = "supabase.storage." + eventName.toLowerCase() + ".";
        String supabaseUrl = this.config.getProperty(this.eventPrefix + "base.url");
        String apiKey =  this.config.getProperty(this.eventPrefix + "api.key");

        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey", apiKey)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

    }

    public byte[] getImagen(String fileName) {
        String endpoint = this.config.getProperty(this.eventPrefix + "endpoint");
        String bucket = this.config.getProperty(this.eventPrefix + "bucket.name");
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