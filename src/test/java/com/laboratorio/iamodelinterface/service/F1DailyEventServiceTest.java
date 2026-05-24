package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.config.GroqLlmConfiguration;
import com.laboratorio.iamodelinterface.config.ChutesEmbeddingConfiguration;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import com.laboratorio.iamodelinterface.config.GeminiLlmConfiguration;
import com.laboratorio.iamodelinterface.config.llmConfiguration;
import com.laboratorio.iamodelinterface.model.EventResponse;
import com.laboratorio.iamodelinterface.util.Constantes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        ChutesLlmConfiguration.class,
        GeminiLlmConfiguration.class,
        GroqLlmConfiguration.class,
        llmConfiguration.class,
        ChutesEmbeddingConfiguration.class,
        SimpleChatService.class,
        F1DailyEventService.class,
        TraduccionService.class,
        SintesisService.class,
        SupabaseStorageService.class,
})
@Slf4j
public class F1DailyEventServiceTest {
    @Autowired
    private F1DailyEventService chatService;

    @Test
    public void f1ChatTest() throws IOException {
        LocalDate fecha = LocalDate.of(2026, 5, 18);

        EventResponse eventResponse = this.chatService.getEventResponse(fecha);

        assertNotNull(eventResponse);
        assertNotEquals(Constantes.WRONG_ANSWER, eventResponse.content());
        assertNotNull(eventResponse.image());
        assertTrue(eventResponse.image().length > 0);

        log.info("Respuesta: {}", eventResponse.content());
        Path destino = Path.of("captura.png");
        Files.write(destino, eventResponse.image());
    }
}