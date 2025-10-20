package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ChutesLlmConfiguration.class, SimpleChatService.class})
@Slf4j
public class SimpleChatServiceTest {
    @Autowired
    private SimpleChatService chatService;

    @Test
    public void simpleChatTest() {
        String prompt = "Hola, ¿cómo te llamas?";

        String respuesta = this.chatService.getChatResponse(prompt);

        assertNotNull(respuesta);

        log.info("Respuesta: {}", respuesta);
    }
}