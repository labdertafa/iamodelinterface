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
public class TechDailyEventService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final SintesisService sintesisService;
    private final SupabaseStorageUtil storageUtil;

    @Value("classpath:prompt/techchat.prompt")
    private Resource promptTemplate;

    public TechDailyEventService(@Qualifier("simpleChatClient") ChatClient chatClient,
                                 @Qualifier("techPgVectorStore") VectorStore vectorStore,
                                 SintesisService sintesisService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.sintesisService = sintesisService;
        this.storageUtil = new SupabaseStorageUtil("techevent");
    }

    public EventResponse getEventResponse(LocalDate date) {
        try {
            String nombreMes = FunctionsUtil.getMonthName(date, IdiomaEnum.CASTELLANO);
            String prompt = String.format("Dime el evento histórico más relevante en el area en las áreas de la tecnología, computación, informática o la innovación en un día como hoy %d de %s",
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

            String respuesta = iaEventResponse.response();
            if (respuesta.length() > Constantes.MAX_SIZE) {
                Thread.sleep(60000);
                try {
                    String sintesis = this.sintesisService.getChatResponse(Constantes.MAX_SIZE, respuesta);
                    if (sintesis.isBlank() || sintesis.equals(Constantes.WRONG_ANSWER)) {
                        return new EventResponse(Constantes.WRONG_ANSWER, null);
                    }
                    log.info("Respuesta sintetizada: {}", sintesis);
                    respuesta = sintesis;
                } catch (Exception e) {
                    log.warn("Error sintetizando la respuesta: {}", e.getMessage());
                    log.warn("No habrá respuesta sintetizada, se usará la traducción original aunque exceda el límite de caracteres");
                }
            }

            String imagenName = FunctionsUtil.getImageName(documentList, iaEventResponse.documentId());
            byte[] image = this.storageUtil.getImagen(imagenName);

            return new EventResponse(
                    "#EnUnDiaComoHoy " + respuesta,
                    image
            );
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo respuesta en el chat especializado en tecnología", e);
        }
    }
}