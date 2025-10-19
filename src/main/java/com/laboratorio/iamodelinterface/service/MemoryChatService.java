package com.laboratorio.iamodelinterface.service;

import com.laboratorio.iamodelinterface.exception.IaModelException;
import com.laboratorio.iamodelinterface.model.IAResponse;
import com.laboratorio.iamodelinterface.model.UserChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemoryChatService {
    @Value("classpath:prompt/memorychat.prompt")
    private Resource promptResource;

    private final ChatClient chatClient;

    public MemoryChatService(@Qualifier("memoryChatClient")ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getChatResponse(UserChatRequest request) {
        try {
            IAResponse iaResponse = this.chatClient
                    .prompt()
                    .user(promptUserSpec -> promptUserSpec
                            .text(this.promptResource)
                            .param("message", request)
                    )
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, request.userId()))
                    .call().entity(IAResponse.class);

            return iaResponse.response();
        } catch (Exception e) {
            throw new IaModelException("Error obteniendo respuesta de chat con memoria", e);
        }
    }
}