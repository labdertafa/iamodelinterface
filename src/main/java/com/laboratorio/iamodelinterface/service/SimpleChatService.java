package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.exception.IaModelException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SimpleChatService {
    private final ChatClient chatClient;

    public SimpleChatService(@Qualifier("simpleChatClient")ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getChatResponse(String prompt) {
        try {
            ChatResponse chatResponse = this.chatClient.prompt(prompt)
                .call().chatResponse();

            return chatResponse != null ?
                    chatResponse.getResult().getOutput().getText() : "No se obtuvo respuesta";
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo respuesta de chat simple", e);
        }
    }
}