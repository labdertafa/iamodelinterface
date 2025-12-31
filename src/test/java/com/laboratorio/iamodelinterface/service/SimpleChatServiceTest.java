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
        String prompt = """
                Dime un evento histórico relevante en las áreas de la tecnología, computación, informática o la innovación en el día 31 de diciembre.
                Si no estás 100% seguro de tu respuesta, contéstame NO ENCONTRE.
                La respuesta debe limitarse a la información que te estoy solicitando y debe tener menos de 280 caracteres.
                """;

        String respuesta = this.chatService.getChatResponse(prompt);

        assertNotNull(respuesta);

        log.info("Respuesta: {}", respuesta);
    }
}