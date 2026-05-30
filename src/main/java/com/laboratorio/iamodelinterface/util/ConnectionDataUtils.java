package com.laboratorio.iamodelinterface.util;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.iamodelinterface.model.ConnectionData;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConnectionDataUtils {
    private ConnectionDataUtils() {}

    public static ConnectionData getPostgreConnectionData(String activeProfiles) {
        String server;
        String bbdd;
        String username;
        String password;

        ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");
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

    public static ConnectionData getSupabaseConnectionData(String activeProfiles, String eventName) {
        String prefixProd = "prod.supabase." + eventName;
        String prefixTest = "test.supabase." + eventName;
        String server;
        String bbdd;
        String username;
        String password;

        ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");
        if (activeProfiles.contains("prod")) {
            server = config.getProperty(prefixProd + ".hostname");
            bbdd = config.getProperty(prefixProd + ".database.name");
            username = config.getProperty(prefixProd + ".username");
            password = config.getProperty(prefixProd + ".password");
        } else {
            server = config.getProperty(prefixTest + ".hostname");
            bbdd = config.getProperty(prefixTest + ".database.name");
            username = config.getProperty(prefixTest + ".username");
            password = config.getProperty(prefixTest + ".password");
        }

        String url = "jdbc:postgresql://" + server + "/" + bbdd;

        return new ConnectionData(username, password, url);
    }

    public static DataSource getPostgreDataSource(ConnectionData cd) {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(cd.url())
                .username(cd.username())
                .password(cd.password())
                .build();
    }

    public static DataSource getEventDataSource(String activeProfiles, String eventName) {
        ConnectionData cd = ConnectionDataUtils.getSupabaseConnectionData(activeProfiles, eventName);
        return getPostgreDataSource(cd);
    }

    public static VectorStore createPgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel,
                                                  String tableName, int nDimensions) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(tableName)
                .initializeSchema(false)
                .indexType(PgVectorStore.PgIndexType.NONE)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .dimensions(nDimensions)
                .build();
    }
}