package com.laboratorio.iamodelinterface.service;

import com.laboratorio.chutes.llm.ChutesChatModel;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import com.laboratorio.iamodelinterface.model.UserChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ChutesLlmConfiguration.class, ChutesChatModel.class, MemoryChatService.class})
@Slf4j
public class MemoryChatServiceTest {
    @Autowired
    private MemoryChatService chatService;

    @Test
    public void memoryChatTest() {
        String prompt = "Hola, soy Rafa, ¿cómo te llamas?";
        UserChatRequest request = new UserChatRequest(UUID.randomUUID().toString(), prompt);

        String respuesta = this.chatService.getChatResponse(request);

        assertNotNull(respuesta);
    }
}