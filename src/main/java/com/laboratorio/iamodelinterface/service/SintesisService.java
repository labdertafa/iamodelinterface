package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.exception.IaModelException;
import com.laboratorio.iamodelinterface.model.IAResponse;
import com.laboratorio.iamodelinterface.util.Constantes;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SintesisService {
    @Value("classpath:prompt/sintesis.prompt")
    private Resource promptTemplate;

    private final ChatClient chatClient;

    public SintesisService(@Qualifier("groqSimpleChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getChatResponse(int maxSize, String prompt) {
        try {
            /* IAResponse iaResponse = this.chatClient.prompt()
                    .user(promptUserSpec -> promptUserSpec
                            .text(this.promptTemplate)
                            .param("max_size", maxSize)
                            .param("entrada", prompt)
                    )
                    .call()
                    .entity(IAResponse.class);

            return iaResponse != null ? iaResponse.response() : Constantes.WRONG_ANSWER; */

            String respuesta = this.chatClient.prompt()
                    .user(promptUserSpec -> promptUserSpec
                            .text(this.promptTemplate)
                            .param("max_size", maxSize)
                            .param("entrada", prompt)
                    )
                    .call()
                    .chatResponse().getResult().getOutput().getText();

            return respuesta != null ? respuesta : Constantes.WRONG_ANSWER;

        } catch (Exception e) {
            throw new IaModelException("Error obteniendo la respuesta de una síntesis", e);
        }
    }
}