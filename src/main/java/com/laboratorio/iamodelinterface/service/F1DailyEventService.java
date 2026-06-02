package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.exception.IaModelException;
import com.laboratorio.iamodelinterface.model.EventResponse;
import com.laboratorio.iamodelinterface.model.IAEventResponse;
import com.laboratorio.iamodelinterface.model.RetrievedDocument;
import com.laboratorio.iamodelinterface.util.Constantes;
import com.laboratorio.iamodelinterface.util.FunctionsUtil;
import com.laboratorio.iamodelinterface.util.IdiomaEnum;
import com.laboratorio.iamodelinterface.util.SupabaseStorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class F1DailyEventService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final TraduccionService traduccionService;
    private final SintesisService sintesisService;
    private final SupabaseStorageUtil storageUtil;

    @Value("classpath:prompt/f1chat.prompt")
    private Resource promptTemplate;

    public F1DailyEventService(@Qualifier("simpleChatClient")ChatClient chatClient,
                               @Qualifier("f1PgVectorStore")VectorStore vectorStore,
                               TraduccionService traduccionService, SintesisService sintesisService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.traduccionService = traduccionService;
        this.sintesisService = sintesisService;
        this.storageUtil = new SupabaseStorageUtil("f1event");
    }

    public EventResponse getEventResponse(LocalDate date) {
        try {
            String nombreMes = FunctionsUtil.getMonthName(date, IdiomaEnum.FRANCES);
            String prompt = String.format("Dis‑moi l’événement le plus important survenu en Formule 1 un jour comme aujourd’hui, %d %s",
                    date.getDayOfMonth(), nombreMes);

            List<RetrievedDocument> documentList = FunctionsUtil.findSimilarDocumentsInSpecificDayOfMonthList(
                    this.vectorStore, prompt, date.getDayOfMonth(), date.getMonthValue());

            String documents = FunctionsUtil.getFormatedDocuments(documentList);

            IAEventResponse iaEventResponse = this.chatClient.prompt()
                    .user(
                            promptUserSpec -> promptUserSpec
                                    .text(this.promptTemplate)
                                    .param("input", prompt)
                                    .param("documents", documents)
                                    .param("text_size", Constantes.TEXT_SIZE)
                    )
                    .call()
                    .entity(IAEventResponse.class);

            if (iaEventResponse == null || iaEventResponse.response().isBlank() || iaEventResponse.documentId() == 0) {
                return new EventResponse(Constantes.WRONG_ANSWER, null);
            }

            log.info("Respuesta original: {}", iaEventResponse.response());

            Thread.sleep(60000);

            String traduccion = this.traduccionService.getChatResponse("Español", iaEventResponse.response());
            if (traduccion.equals(Constantes.WRONG_ANSWER)) {
                return new EventResponse(Constantes.WRONG_ANSWER, null);
            }

            log.info("Respuesta traducida: {}", traduccion);

            if (traduccion.length() > Constantes.MAX_SIZE) {
                Thread.sleep(60000);
                try {
                    String sintesis = this.sintesisService.getChatResponse(Constantes.MAX_SIZE, traduccion);
                    if (sintesis.isBlank() || sintesis.equals(Constantes.WRONG_ANSWER)) {
                        return new EventResponse(Constantes.WRONG_ANSWER, null);
                    }
                    log.info("Respuesta sintetizada: {}", sintesis);
                    traduccion = sintesis;
                } catch (Exception e) {
                    log.warn("Error sintetizando la respuesta: {}", e.getMessage());
                    log.warn("No habrá respuesta sintetizada, se usará la traducción original aunque exceda el límite de caracteres");
                }
            }

            String imagenName = FunctionsUtil.getImageName(documentList, iaEventResponse.documentId());
            byte[] image = this.storageUtil.getImagen(imagenName);

            return new EventResponse(
                    "#EnUnDiaComoHoy " + traduccion,
                    image
            );
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo respuesta en el chat especializado en F1", e);
        }
    }
}