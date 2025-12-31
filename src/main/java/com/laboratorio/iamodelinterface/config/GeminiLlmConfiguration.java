package com.laboratorio.iamodelinterface.config;

import com.google.genai.Client;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import({MemoryDataSourceConfig.class})
public class GeminiLlmConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Bean(name = "geminiChatModel")
    public ChatModel geminiChatModel() {
        String apiKey = this.config.getProperty("gemini_bearer_token");
        String model = this.config.getProperty("gemini_text_model");
        Double temperature = Double.valueOf(this.config.getProperty("gemini_text_temperature"));
        Integer maxTokens = Integer.valueOf(this.config.getProperty("gemini_text_max_tokens"));

        return GoogleGenAiChatModel.builder()
                .genAiClient(
                        Client.builder()
                                .apiKey(apiKey)
                                .build()
                )
                .defaultOptions(
                        GoogleGenAiChatOptions.builder()
                                .model(model)
                                .temperature(temperature)
                                .maxOutputTokens(maxTokens)
                                .build()
                )
                .build();
    }

    @Bean(name = "geminiSimpleChatClient")
    @Primary
    public ChatClient geminiSimpleChatClient(@Qualifier("geminiChatModel")ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean(name = "geminiMemoryChatClient")
    public ChatClient geminiMemoryChatClient(@Qualifier("geminiChatModel")ChatModel chatModel,
                                             @Qualifier("labrafaChatMemory")ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}