package com.laboratorio.iamodelinterface.config;

import com.laboratorio.chutes.embedding.ChutesEmbeddingModel;
import com.laboratorio.chutes.embedding.config.ChutesEmbeddingApi;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.iamodelinterface.util.ConnectionDataUtils;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ChutesEmbeddingConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Primary
    @Bean("ollamaEmbeddingModel")
    public EmbeddingModel ollamaEmbeddingModel() {
        String baseUrl = this.config.getProperty("ollama.embedding.base.url");
        String model = this.config.getProperty("ollama.embedding.model");
        int nDimensions = Integer.parseInt(this.config.getProperty("ollama.embedding.dimensions"));

        OllamaApi api = OllamaApi.builder()
                .baseUrl(baseUrl)
                .build();

        OllamaEmbeddingOptions options = OllamaEmbeddingOptions.builder()
                .model(model)
                .dimensions(nDimensions)
                .build();

        return OllamaEmbeddingModel.builder()
                .ollamaApi(api)
                .options(options)
                .build();
    }

    @Bean
    public ChutesEmbeddingApi chutesEmbeddingApi() {
        String apiKey = this.config.getProperty("chutes_bearer_token");
        String baseUrl = this.config.getProperty("embedding.url");
        String uri = this.config.getProperty("embedding.uri");
        return new ChutesEmbeddingApi(apiKey, baseUrl, uri);
    }

    @Bean(name = "chutesEmbeddingModel")
    public EmbeddingModel embeddingModel(ChutesEmbeddingApi embeddingApi) {
        return new ChutesEmbeddingModel(embeddingApi);
    }

    @Primary
    @Bean(name = "pgVectorStore")
    public VectorStore pgVectorStore(@Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate,
                                    @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        String tableName = this.config.getProperty("f1_daily_event.table.name");
        int nDimensions = Integer.parseInt(this.config.getProperty("embedding.dimensions"));

        return ConnectionDataUtils.createPgVectorStore(jdbcTemplate, embeddingModel, tableName, nDimensions);
    }

    @Bean(name = "f1PgVectorStore")
    public VectorStore f1PgVectorStore(@Qualifier("supabaseF1TechJdbcTemplate") JdbcTemplate jdbcTemplate,
                                     @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        String tableName = this.config.getProperty("f1_daily_event.table.name");
        int nDimensions = Integer.parseInt(this.config.getProperty("embedding.dimensions"));

        return ConnectionDataUtils.createPgVectorStore(jdbcTemplate, embeddingModel, tableName, nDimensions);
    }

    @Bean(name = "techPgVectorStore")
    public VectorStore techPgVectorStore(@Qualifier("supabaseTechEventJdbcTemplate") JdbcTemplate jdbcTemplate,
                                       @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        String tableName = this.config.getProperty("tech_daily_event.table.name");
        int nDimensions = Integer.parseInt(this.config.getProperty("embedding.dimensions"));

        return ConnectionDataUtils.createPgVectorStore(jdbcTemplate, embeddingModel, tableName, nDimensions);
    }
}