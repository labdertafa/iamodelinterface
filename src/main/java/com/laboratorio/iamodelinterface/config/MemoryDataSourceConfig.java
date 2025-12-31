package com.laboratorio.iamodelinterface.config;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.iamodelinterface.model.ConnectionData;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MemoryDataSourceConfig {
    @Value("${spring.profiles.active:default}")
    private String activeProfiles;

    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    private ConnectionData getConnectionData() {
        String server;
        String bbdd;
        String username;
        String password;
        if (activeProfiles.contains("prod")) {
            server = config.getProperty("servidor_pgvector");
            bbdd = config.getProperty("bbdd_pg_vector");
            username = config.getProperty("usuario_pgvector");
            password = config.getProperty("password_pgvector");
        } else {
            server = config.getProperty("servidor_pgvector_test");
            bbdd = config.getProperty("bbdd_pg_vector_test");
            username = config.getProperty("usuario_pgvector_test");
            password = config.getProperty("password_pgvector_test");
        }

        String url = "jdbc:postgresql://" + server + "/" + bbdd;

        return new ConnectionData(username, password, url);
    }

    @Bean(name = "pgVectorDataSource")
    public DataSource dataSource() {
        ConnectionData cd = this.getConnectionData();
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(cd.url())
                .username(cd.username())
                .password(cd.password())
                .build();
    }

    @Bean(name = "pgVectorJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("pgVectorDataSource")DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "labrafaChatMemory")
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
}