package com.szubp.mongodb_replica_set_ha.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * Exposes three MongoTemplate beans for different read/write concern scenarios:
 *   mongoTemplate          — PRIMARY reads, w:majority writes (default)
 *   secondaryMongoTemplate — SECONDARY_PREFERRED reads
 *   w1MongoTemplate        — w:1 writes (low-latency)
 */
@Configuration
public class MongoConfig {

    @Value("${app.mongodb.uri}")
    private String mongoUri;

    @Value("${app.mongodb.database}")
    private String databaseName;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .readPreference(ReadPreference.primary())
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoTemplate template = new MongoTemplate(mongoDatabaseFactory);
        template.setWriteConcern(WriteConcern.MAJORITY);
        return template;
    }

    // Override getMongoDatabase() to attach secondaryPreferred to every read
    @Bean("secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate(MongoClient mongoClient) {
        SimpleMongoClientDatabaseFactory secondaryFactory =
                new SimpleMongoClientDatabaseFactory(mongoClient, databaseName) {
                    @Override
                    public MongoDatabase getMongoDatabase() throws DataAccessException {
                        return super.getMongoDatabase().withReadPreference(ReadPreference.secondaryPreferred());
                    }

                    @Override
                    public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
                        return super.getMongoDatabase(dbName).withReadPreference(ReadPreference.secondaryPreferred());
                    }
                };
        return new MongoTemplate(secondaryFactory);
    }

    @Bean("w1MongoTemplate")
    public MongoTemplate w1MongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoTemplate template = new MongoTemplate(mongoDatabaseFactory);
        template.setWriteConcern(WriteConcern.W1);
        return template;
    }
}

