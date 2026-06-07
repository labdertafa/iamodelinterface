package com.laboratorio.iamodelinterface.config;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.iamodelinterface.model.IAProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ChutesLlmConfiguration.class,
        GeminiLlmConfiguration.class,
        GroqLlmConfiguration.class,
        RouterLlmConfiguration.class
})
public class llmConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    private IAProvider getIAProvider() {
        String iaProviderName = this.config.getProperty("ia_provider");
        return IAProvider.from(iaProviderName);
    }

    @Bean(name = "simpleChatClient")
    public ChatClient simpleChatClient(@Qualifier("chutesSimpleChatClient") ChatClient chutesChatClient,
                                       @Qualifier("geminiSimpleChatClient") ChatClient geminiChatClient,
                                       @Qualifier("groqSimpleChatClient") ChatClient groqChatClient,
                                       @Qualifier("routerSimpleChatClient") ChatClient routerChatClient) {

        IAProvider provider = this.getIAProvider();
        return  switch (provider) {
            case CHUTES -> chutesChatClient;
            case GEMINI -> geminiChatClient;
            case GROQ -> groqChatClient;
            case ROUTER -> routerChatClient;
        };
    }

    @Bean(name = "memoryChatClient")
    public ChatClient memoryChatClient(@Qualifier("chutesMemoryChatClient") ChatClient chutesChatClient,
                                       @Qualifier("geminiMemoryChatClient") ChatClient geminiChatClient,
                                       @Qualifier("groqMemoryChatClient") ChatClient groqChatClient,
                                       @Qualifier("routerMemoryChatClient") ChatClient routerChatClient) {

        IAProvider provider = this.getIAProvider();
        return  switch (provider) {
            case CHUTES -> chutesChatClient;
            case GEMINI -> geminiChatClient;
            case GROQ -> groqChatClient;
            case ROUTER -> routerChatClient;
        };
    }
}