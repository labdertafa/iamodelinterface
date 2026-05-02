package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.GroqLlmConfiguration;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import com.laboratorio.iamodelinterface.config.GeminiLlmConfiguration;
import com.laboratorio.iamodelinterface.config.llmConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        ChutesLlmConfiguration.class,
        GeminiLlmConfiguration.class,
        GroqLlmConfiguration.class,
        llmConfiguration.class,
        SimpleChatService.class,
        TraduccionService.class
})
public class TraduccionServiceTest {
    @Autowired
    private TraduccionService service;

    @Test
    public void simpleTraductionTest() {
        String prompt = "el gato negro";

        String response = this.service.getChatResponse("francés", prompt);

        assertNotNull(response);
        String esperado = "LE CHAT NOIR";
        assertEquals(esperado, response.toUpperCase());
    }
}