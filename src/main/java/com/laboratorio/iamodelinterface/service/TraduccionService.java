package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.exception.IaModelException;
import com.laboratorio.iamodelinterface.model.IAResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class TraduccionService {
    @Value("classpath:prompt/traduccion.prompt")
    private Resource promptTemplate;

    private final ChatClient chatClient;

    public TraduccionService(@Qualifier("simpleChatClient")ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getChatResponse(String language, String prompt) {
        try {
            IAResponse iaResponse = this.chatClient.prompt()
                    .user(promptUserSpec -> promptUserSpec
                            .text(this.promptTemplate)
                            .param("idioma", language)
                            .param("entrada", prompt)
                    )
                    .call()
                    .entity(IAResponse.class);

            return iaResponse != null ? iaResponse.response() : "No se obtuvo respuesta";
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo la respuesta de una traducción", e);
        }
    }
}