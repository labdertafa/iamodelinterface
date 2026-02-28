package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.exception.IaModelException;
import com.laboratorio.iamodelinterface.model.IAResponse;
import com.laboratorio.iamodelinterface.util.Constantes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class F1DailyEventService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final TraduccionService traduccionService;
    private final SintesisService sintesisService;

    @Value("classpath:prompt/f1chat.prompt")
    private Resource promptTemplate;

    public F1DailyEventService(@Qualifier("simpleChatClient")ChatClient chatClient,
                               @Qualifier("F1PgVectorStore")VectorStore vectorStore,
                               TraduccionService traduccionService, SintesisService sintesisService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.traduccionService = traduccionService;
        this.sintesisService = sintesisService;
    }

    public String getEventResponse(LocalDate date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("fr-FR"));
            String nombreMes = date.format(formatter);
            String prompt = String.format("Dis‑moi l’événement le plus important survenu en Formule 1 un jour comme aujourd’hui, %d %s",
                    date.getDayOfMonth(), nombreMes);

            String documents = String.join("\n", this.findSimilarDocuments(prompt,
                    date.getDayOfMonth(), date.getMonthValue()));

            IAResponse iaResponse = this.chatClient.prompt()
                    .user(promptUserSpec -> promptUserSpec
                            .text(this.promptTemplate)
                            .param("input", prompt)
                            .param("documents", documents)
                    )
                    .call()
                    .entity(IAResponse.class);

            if (iaResponse == null) {
                return Constantes.WRONG_ANSWER;
            }

            log.debug("Respuesta original: {}", iaResponse.response());

            String traduccion = this.traduccionService.getChatResponse("Español", iaResponse.response());
            if (traduccion.equals(Constantes.WRONG_ANSWER)) {
                return Constantes.WRONG_ANSWER;
            }

            log.debug("Respuesta traducida: {}", traduccion);

            if (traduccion.length() > Constantes.MAX_SIZE) {
                traduccion = this.sintesisService.getChatResponse(Constantes.MAX_SIZE, traduccion);
                log.debug("Respuesta sintetizada: {}", traduccion);
            }

            return "#EnUnDiaComoHoy " + traduccion;
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo respuesta en el chat especializado en F1", e);
        }
    }

    private List<String> findSimilarDocuments(String query, int day, int month) {
        String filterExpr = String.format("contextDay == %d AND contextMonth == %d", day, month);

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(50)
                .filterExpression(filterExpr)
                .build();

        List<Document> documents = this.vectorStore.similaritySearch(request);

        return documents.stream()
                .map(Document::getFormattedContent)
                .toList();
    }
}