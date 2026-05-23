package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.config.GroqLlmConfiguration;
import com.laboratorio.iamodelinterface.config.ChutesLlmConfiguration;
import com.laboratorio.iamodelinterface.config.GeminiLlmConfiguration;
import com.laboratorio.iamodelinterface.config.llmConfiguration;
import com.laboratorio.iamodelinterface.util.Constantes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        ChutesLlmConfiguration.class,
        GeminiLlmConfiguration.class,
        GroqLlmConfiguration.class,
        llmConfiguration.class,
        SimpleChatService.class,
        SintesisService.class
})
@Slf4j
public class SintesisServiceTest {
    @Autowired
    private SintesisService sintesisService;

    @Test
    public void simpleSintesisServiceTest() {
        String prompt = """
                El 24 de febrero, varios eventos destacados ocurrieron en la Fórmula 1:
                nacimientos de pilotos como Alain Prost (1955), Pedro de la Rosa (1971) y Emanuele Naspetti (1968),
                así como el fallecimiento de Dave Charlton (2013).
                El evento más importante es el nacimiento de Alain Prost, cuádruple campeón del mundo y leyenda de la F1.
                """;

        String response = this.sintesisService.getChatResponse(Constantes.MAX_SIZE, prompt);

        log.info("Síntesis {} caracteres: {}", response.length(), response);

        assertNotNull(response);
        assertTrue(response.length() <= Constantes.MAX_SIZE);
    }
}