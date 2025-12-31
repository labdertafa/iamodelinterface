package com.laboratorio.iamodelinterface.config;

import com.laboratorio.chutes.embedding.ChutesEmbeddingModel;
import com.laboratorio.chutes.embedding.config.ChutesEmbeddingApi;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ChutesEmbeddingConfiguration {
    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Bean
    public ChutesEmbeddingApi chutesEmbeddingApi() {
        String apiKey = this.config.getProperty("chutes_bearer_token");
        String baseUrl = this.config.getProperty("embedding.url");
        String uri = this.config.getProperty("embedding.uri");
        return new ChutesEmbeddingApi(apiKey, baseUrl, uri);
    }

    @Bean(name = "chutesEmbeddingModel")
    @Primary
    public EmbeddingModel embeddingModel(ChutesEmbeddingApi embeddingApi) {
        return new ChutesEmbeddingModel(embeddingApi);
    }

    @Bean(name = "F1PgVectorStore")
    public VectorStore VectorStore(@Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate,
                                     @Qualifier("chutesEmbeddingModel")EmbeddingModel embeddingModel) {
        String tableName = this.config.getProperty("f1_daily_event.table.name");
        int nDimensions = Integer.parseInt(this.config.getProperty("embedding.dimensions"));

        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(tableName)
                .initializeSchema(true)
                .indexType(PgVectorStore.PgIndexType.NONE)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .dimensions(nDimensions)
                .build();
    }
}