package com.laboratorio.iamodelinterface.service;

import com.laboratorio.chutes.llm.ChutesChatModel;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        ChutesLlmConfiguration.class,
        ChutesChatModel.class,
        SimpleChatService.class,
        TraduccionService.class
})
@Slf4j
public class TraduccionServiceTest {
    @Autowired
    private TraduccionService service;

    @Test
    public void simpleTraductionTest() {
        String prompt = "gato";

        String response = this.service.getChatResponse("francés", prompt);

        assertNotNull(response);
        String esperado = "CHAT";
        assertEquals(esperado, response.toUpperCase());
    }
}