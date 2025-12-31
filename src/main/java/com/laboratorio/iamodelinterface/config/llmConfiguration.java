package com.laboratorio.iamodelinterface.config;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ChutesLlmConfiguration.class, GeminiLlmConfiguration.class})
public class llmConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Bean(name = "simpleChatClient")
    public ChatClient simpleChatClient(@Qualifier("chutesSimpleChatClient")ChatClient chutesChatClient,
                                       @Qualifier("geminiSimpleChatClient")ChatClient geminiChatClient) {
        String iaProvider = this.config.getProperty("ia_provider");

        return  switch (iaProvider.toLowerCase()) {
            case "chutes" -> chutesChatClient;
            case "gemini" -> geminiChatClient;
            default -> throw new IllegalArgumentException("Proveedor de IA desconocido: " + iaProvider);
        };
    }

    @Bean(name = "memoryChatClient")
    public ChatClient memoryChatClient(@Qualifier("chutesMemoryChatClient")ChatClient chutesChatClient,
                                       @Qualifier("geminiMemoryChatClient")ChatClient geminiChatClient) {
        String iaProvider = this.config.getProperty("ia_provider");

        return  switch (iaProvider.toLowerCase()) {
            case "chutes" -> chutesChatClient;
            case "gemini" -> geminiChatClient;
            default -> throw new IllegalArgumentException("Proveedor de IA desconocido: " + iaProvider);
        };
    }
}