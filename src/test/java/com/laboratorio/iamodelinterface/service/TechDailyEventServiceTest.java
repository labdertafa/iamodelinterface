package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.config.GroqLlmConfiguration;
import com.laboratorio.iamodelinterface.config.ChutesEmbeddingConfiguration;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import com.laboratorio.iamodelinterface.config.GeminiLlmConfiguration;
import com.laboratorio.iamodelinterface.config.llmConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        ChutesLlmConfiguration.class,
        GeminiLlmConfiguration.class,
        GroqLlmConfiguration.class,
        llmConfiguration.class,
        ChutesEmbeddingConfiguration.class,
        SimpleChatService.class,
        TechDailyEventService.class,
        SintesisService.class
})
@Slf4j
public class TechDailyEventServiceTest {
    @Autowired
    private TechDailyEventService chatService;

     @Test
    public void techChatTest() {
         LocalDate fecha = LocalDate.of(2026, 5, 9);

         String respuesta = this.chatService.getEventResponse(fecha);

         assertNotNull(respuesta);

         log.info("Respuesta: {}", respuesta);
     }
}