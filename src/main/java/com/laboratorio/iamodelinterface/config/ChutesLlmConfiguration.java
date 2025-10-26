package com.laboratorio.iamodelinterface.config;

import com.laboratorio.chutes.llm.ChutesChatModel;
import com.laboratorio.chutes.llm.config.ChutesLlmApi;
import com.laboratorio.chutes.llm.config.ChutesLlmOptions;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ChutesLlmConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Bean
    public ChutesLlmApi chutesLlmApi() {
        String apiKey = this.config.getProperty("bearer_token");
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

    @Bean(name = "simpleChatClient")
    @Primary
    public ChatClient simpleChatClient(@Qualifier("chutesChatModel")ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean(name = "pgVectorDataSource")
    public DataSource dataSource() {
        String server = config.getProperty("servidor_pgvector");
        String bbdd = config.getProperty("bbdd_pg_vector");
        String username = config.getProperty("usuario_pgvector");
        String password = config.getProperty("password_pgvector");
        String url = "jdbc:postgresql://" + server + "/" + bbdd;
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "pgVectorJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("pgVectorDataSource")DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "chutesChatMemory")
    @Primary
    public ChatMemory chatMemory(@Qualifier("pgVectorJdbcTemplate")JdbcTemplate jdbcTemplate) {
        ChatMemoryRepository repository = JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new PostgresChatMemoryRepositoryDialect())
                .build();

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(40)
                .build();
    }

    @Bean(name = "memoryChatClient")
    public ChatClient memoryChatClient(@Qualifier("chutesChatModel")ChatModel chatModel,
                                       @Qualifier("chutesChatMemory")ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean(name = "chutesEmbeddingModel")
    @Primary
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .ollamaApi(
                        new OllamaApi.Builder()
                                .baseUrl("http://localhost:11434")
                                .build()
                )
                .defaultOptions(
                        OllamaOptions.builder()
                                .model("mxbai-embed-large")
                                .build()
                )
                .build();
    }

    @Bean(name = "F1PgVectorStore")
    public PgVectorStore pgVectorStore(@Qualifier("pgVectorJdbcTemplate")JdbcTemplate jdbcTemplate,
                                       @Qualifier("chutesEmbeddingModel")EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("daily_f1_data")
                .initializeSchema(true)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .dimensions(1024)
                .build();
    }
}