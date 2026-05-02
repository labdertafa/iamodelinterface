package com.laboratorio.iamodelinterface;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.iamodelinterface.config.MemoryDataSourceConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MemoryDataSourceConfig.class})
public class GroqLlmConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Bean(name = "groqChatModel")
    public ChatModel groqChatModel() {
        String baseUrl = this.config.getProperty("groq_base_url");
        String apiKey = this.config.getProperty("groq_api_key");
        String model = this.config.getProperty("groq_text_model");
        Double temperature = Double.valueOf(this.config.getProperty("groq_text_temperature"));
        Integer maxTokens = Integer.valueOf(this.config.getProperty("groq_text_max_tokens"));

        return OpenAiChatModel.builder()
                .openAiApi(
                        OpenAiApi.builder()
                                .baseUrl(baseUrl)
                                .apiKey(apiKey)
                                .build()
                )
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model(model)
                                .temperature(temperature)
                                .maxTokens(maxTokens)
                                .build()
                )
                .build();
    }

    @Bean(name = "groqSimpleChatClient")
    public ChatClient groqSimpleChatClient(@Qualifier("groqChatModel") ChatModel groqChatModel) {
        return ChatClient.create(groqChatModel);
    }

    @Bean(name = "groqMemoryChatClient")
    public ChatClient groqMemoryChatClient(@Qualifier("groqChatModel") ChatModel groqChatModel,
                                              @Qualifier("labrafaChatMemory") ChatMemory chatMemory) {
        return ChatClient.builder(groqChatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build()
                )
                .build();
    }
}