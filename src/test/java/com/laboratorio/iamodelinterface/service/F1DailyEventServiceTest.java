package com.laboratorio.iamodelinterface.service;

import com.laboratorio.chutes.llm.ChutesChatModel;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        ChutesLlmConfiguration.class,
        ChutesChatModel.class,
        SimpleChatService.class,
        F1DailyEventService.class})
@Slf4j
public class F1DailyEventServiceTest {
    @Autowired
    private F1DailyEventService chatService;

    @Test
    public void f1ChatTest() {
        LocalDate fecha = LocalDate.of(2025, 1, 11);

        String respuesta = this.chatService.getEventResponse(fecha);

        assertNotNull(respuesta);

        log.info("Respuesta: {}", respuesta);

        assertTrue(respuesta.length() <= 240);
    }
}