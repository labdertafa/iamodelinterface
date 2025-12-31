package com.laboratorio.iamodelinterface.config;

import com.laboratorio.chutes.llm.ChutesChatModel;
import com.laboratorio.chutes.llm.config.ChutesLlmApi;
import com.laboratorio.chutes.llm.config.ChutesLlmOptions;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import({MemoryDataSourceConfig.class})
public class ChutesLlmConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Bean
    public ChutesLlmApi chutesLlmApi() {
        String apiKey = this.config.getProperty("chutes_bearer_token");
        String baseUrl = this.config.getProperty("text_baseurl");
        String uri = this.config.getProperty("text_uri");
        return new ChutesLlmApi(apiKey, baseUrl, uri);
    }

    @Bean
    public ChutesLlmOptions chutesLlmOptions() {
        String model = this.config.getProperty("text_model");
        Integer maxTokens = Integer.valueOf(this.config.getProperty("text_max_tokens"));
        Double temperature = Double.valueOf(this.config.getProperty("text_temperature"));
        Integer n = Integer.valueOf(this.config.getProperty("text_n"));

        return new ChutesLlmOptions(model, maxTokens, temperature, n);
    }

    @Bean(name = "chutesChatModel")
    @Primary
    public ChatModel chutesChatModel(ChutesLlmApi api, ChutesLlmOptions options) {
        return new ChutesChatModel(api, options);
    }

    @Bean(name = "chutesSimpleChatClient")
    public ChatClient chutesSimpleChatClient(@Qualifier("chutesChatModel")ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean(name = "chutesMemoryChatClient")
    public ChatClient chutesMemoryChatClient(@Qualifier("chutesChatModel")ChatModel chatModel,
                                       @Qualifier("labrafaChatMemory")ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}