package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.exception.IaModelException;
import com.laboratorio.iamodelinterface.model.IAResponse;
import com.laboratorio.iamodelinterface.util.Constantes;
import com.laboratorio.iamodelinterface.util.FunctionsUtil;
import com.laboratorio.iamodelinterface.util.IdiomaEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class F1DailyEventService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final TraduccionService traduccionService;
    private final SintesisService sintesisService;

    @Value("classpath:prompt/f1chat.prompt")
    private Resource promptTemplate;

    public F1DailyEventService(@Qualifier("groqSimpleChatClient")ChatClient chatClient,
                               @Qualifier("F1PgVectorStore")VectorStore vectorStore,
                               TraduccionService traduccionService, SintesisService sintesisService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.traduccionService = traduccionService;
        this.sintesisService = sintesisService;
    }

    public String getEventResponse(LocalDate date) {
        try {
            String nombreMes = FunctionsUtil.getMonthName(date, IdiomaEnum.FRANCES);
            String prompt = String.format("Dis‑moi l’événement le plus important survenu en Formule 1 un jour comme aujourd’hui, %d %s",
                    date.getDayOfMonth(), nombreMes);

            String documents = String.join("\n", FunctionsUtil.findSimilarDocumentsInSpecificDayOfMonth(
                    this.vectorStore, prompt, date.getDayOfMonth(), date.getMonthValue()));

            IAResponse iaResponse = this.chatClient.prompt()
                    .user(
                            promptUserSpec -> promptUserSpec
                                    .text(this.promptTemplate)
                                    .param("input", prompt)
                                    .param("documents", documents)
                    )
                    .call()
                    .entity(IAResponse.class);

            if (iaResponse == null || iaResponse.response().isBlank()) {
                return Constantes.WRONG_ANSWER;
            }

            log.info("Respuesta original: {}", iaResponse.response());

            Thread.sleep(60000);

            String traduccion = this.traduccionService.getChatResponse("Español", iaResponse.response());
            if (traduccion.isBlank() || traduccion.equals(Constantes.WRONG_ANSWER)) {
                log.error("Error haciendo la traducción de la respuesta, se obtuvo una respuesta vacía o incorrecta");
                return Constantes.WRONG_ANSWER;
            }

            log.info("Respuesta traducida: {}", traduccion);

            if (traduccion.length() > Constantes.MAX_SIZE) {
                Thread.sleep(60000);
                traduccion = this.sintesisService.getChatResponse(Constantes.MAX_SIZE, traduccion);
                if (traduccion.isBlank() || traduccion.equals(Constantes.WRONG_ANSWER)) {
                    log.error("Error haciendo la síntesis de la respuesta, se obtuvo una respuesta vacía o incorrecta");
                    return Constantes.WRONG_ANSWER;
                }
                log.info("Respuesta sintetizada: {}", traduccion);
            }

            return "#EnUnDiaComoHoy " + traduccion;
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo respuesta en el chat especializado en F1", e);
        }
    }
}