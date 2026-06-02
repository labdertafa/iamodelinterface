package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.config.GroqLlmConfiguration;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import com.laboratorio.iamodelinterface.config.GeminiLlmConfiguration;
import com.laboratorio.iamodelinterface.config.MemoryDataSourceConfig;
import com.laboratorio.iamodelinterface.config.llmConfiguration;
import com.laboratorio.iamodelinterface.model.UserChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        MemoryDataSourceConfig.class,
        ChutesLlmConfiguration.class,
        GeminiLlmConfiguration.class,
        GroqLlmConfiguration.class,
        GroqLlmConfiguration.class,
        llmConfiguration.class,
        MemoryChatService.class
})
@Slf4j
public class MemoryChatServiceTest {
    @Autowired
    private MemoryChatService chatService;

    @Test
    public void memoryChatTest() {
        String prompt = "Eres un profesor de programación experto en Java, C++, Python y SQL. Dame un consejo de buenas prácticas que pueda dar a los estudiantes en cualquiera de estos lenguajes. Evita darme información que ya me hayas dado antes. La respuesta debe limitarse a la información que te estoy solicitando, comenzar con los hashtags #Programacion #BuenasPracticas y debe tener menos de 270 caracteres.";
        UserChatRequest request = new UserChatRequest(UUID.randomUUID().toString(), prompt);

        String respuesta = this.chatService.getChatResponse(request);

        assertNotNull(respuesta);

        log.info("Respuesta: {}", respuesta);
    }
}