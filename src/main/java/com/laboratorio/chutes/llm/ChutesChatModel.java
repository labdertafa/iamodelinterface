package com.laboratorio.chutes.llm;

import com.laboratorio.chutes.exception.ChutesException;
import com.laboratorio.chutes.llm.config.ChutesLlmApi;
import com.laboratorio.chutes.llm.config.ChutesLlmOptions;
import com.laboratorio.chutes.llm.model.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ChutesChatModel implements ChatModel {
    private final ChutesLlmApi chutesLlmApi;
    private final ChutesLlmOptions llmOptions;
    private final RestClient restClient;

    public ChutesChatModel(ChutesLlmApi chutesLlmApi, ChutesLlmOptions llmOptions) {
        this.chutesLlmApi = chutesLlmApi;
        this.llmOptions = llmOptions;

        this.restClient = RestClient.builder()
                .baseUrl(this.chutesLlmApi.baseUrl())
                .defaultHeader("Authorization", "Bearer " + this.chutesLlmApi.apiKey())
                .build();
    }

    @Override
    @NonNull
    public ChatResponse call(Prompt prompt) {
        try {
            // 1. Convertir el Prompt de Spring AI al formato que espera Chutes
            ChutesChatRequest request = this.createRequestFromPrompt(prompt);

            // 2. Llamar a la API
            ChutesChatResponse response = this.restClient.post()
                    .uri(this.chutesLlmApi.uri())
                    .body(request)
                    .retrieve()
                    .body(ChutesChatResponse.class);

            if (response == null) {
                throw new ChutesException( "La respuesta de Chutes AI ha sido nula");
            }
            if ((response.choices() == null) || (response.choices().isEmpty())) {
                throw new ChutesException( "No hubo una respuesta válida de Chutes  AI");
            }

            // 3. Convertir la respuesta de Chutes al ChatResponse de Spring AI
            return this.convertResponseToChatResponse(response);
        } catch (Exception e) {
            throw new ChutesException("Ocurrió un error al llamar a Chutes AI", e);
        }
    }

    private ChutesChatRequest createRequestFromPrompt(Prompt prompt) {
        List<ChutesMessage> messages = prompt.getInstructions().stream()
                .map(message -> new ChutesMessage(
                        message.getMessageType().getValue(),
                        message.getText()
                ))
                .toList();

        return new ChutesChatRequest(this.llmOptions.model(), messages, this.llmOptions.maxTokens(),
                this.llmOptions.temperature(), this.llmOptions.n());
    }

    private ChatResponse convertResponseToChatResponse(ChutesChatResponse response) {
        List<Generation> generations = response.choices().stream()
                .map(choice -> new Generation(
                        new AssistantMessage(choice.message().content()))
                )
                .toList();

        return new ChatResponse(generations);
    }
}