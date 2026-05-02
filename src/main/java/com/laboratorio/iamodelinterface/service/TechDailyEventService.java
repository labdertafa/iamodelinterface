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
public class TechDailyEventService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final SintesisService sintesisService;

    @Value("classpath:prompt/techchat.prompt")
    private Resource promptTemplate;

    public TechDailyEventService(@Qualifier("groqSimpleChatClient") ChatClient chatClient,
                                 @Qualifier("TechPgVectorStore") VectorStore vectorStore,
                                 SintesisService sintesisService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.sintesisService = sintesisService;
    }

    public String getEventResponse(LocalDate date) {
        try {
            String nombreMes = FunctionsUtil.getMonthName(date, IdiomaEnum.CASTELLANO);
            String prompt = String.format("Dime el evento histórico más relevante en el area en las áreas de la tecnología, computación, informática o la innovación en un día como hoy %d de %s",
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

            log.debug("Respuesta original: {}", iaResponse.response());

            String sintesis = iaResponse.response();
            if (sintesis.length() > Constantes.MAX_SIZE) {
                sintesis = this.sintesisService.getChatResponse(Constantes.MAX_SIZE, sintesis);
                log.debug("Respuesta sintetizada: {}", sintesis);
            }

            return "#EnUnDiaComoHoy " + sintesis;
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo respuesta en el chat especializado en tecnología", e);
        }
    }
}