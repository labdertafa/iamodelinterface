package com.laboratorio.iamodelinterface.service;

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
        llmConfiguration.class,
        ChutesEmbeddingConfiguration.class,
        SimpleChatService.class,
        F1DailyEventService.class,
        TraduccionService.class
})
@Slf4j
public class F1DailyEventServiceTest {
    @Autowired
    private F1DailyEventService chatService;

    @Test
    public void f1ChatTest() {
        LocalDate fecha = LocalDate.now();

        String respuesta = this.chatService.getEventResponse(fecha);

        assertNotNull(respuesta);

        log.info("Respuesta: {}", respuesta);
  }
}