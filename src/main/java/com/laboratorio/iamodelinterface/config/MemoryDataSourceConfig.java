package com.laboratorio.iamodelinterface.config;

import com.laboratorio.iamodelinterface.model.ConnectionData;
import com.laboratorio.iamodelinterface.util.ConnectionDataUtils;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MemoryDataSourceConfig {
    @Value("${spring.profiles.active:default}")
    private String activeProfiles;

    @Primary
    @Bean(name = "pgVectorDataSource")
    public DataSource dataSource() {
        ConnectionData cd = ConnectionDataUtils.getPostgreConnectionData(this.activeProfiles);
        return ConnectionDataUtils.getPostgreDataSource(cd);
    }

    @Bean(name = "supabaseF1EventDataSource")
    public DataSource supabaseF1EventDataSource() {
        return ConnectionDataUtils.getEventDataSource(this.activeProfiles, "f1event");
    }

    @Bean(name = "supabaseTechEventDataSource")
    public DataSource supabaseTechEventDataSource() {
        return ConnectionDataUtils.getEventDataSource(this.activeProfiles, "techevent");
    }

    @Primary
    @Bean(name = "pgVectorJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("pgVectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "supabaseF1TechJdbcTemplate")
    public JdbcTemplate supabaseF1TechJdbcTemplate(@Qualifier("supabaseF1EventDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "supabaseTechEventJdbcTemplate")
    public JdbcTemplate supabaseTechEventJdbcTemplate(@Qualifier("supabaseTechEventDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "labrafaChatMemory")
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