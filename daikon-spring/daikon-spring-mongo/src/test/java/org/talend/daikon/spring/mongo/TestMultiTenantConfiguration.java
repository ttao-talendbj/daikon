package org.talend.daikon.spring.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class TestMultiTenantConfiguration {

    private static final ThreadLocal<String> dataBaseName = ThreadLocal.withInitial(() -> "default");

    static void changeTenant(String tenant) {
        dataBaseName.set(tenant);
    }

    @Bean
    public MongoClient mongoClient(Fongo fongo) {
        // Create a MongoClient over a fake MongoDB.
        return fongo.getMongo();
    }

    @Bean
    public MongoDbFactory defaultMongoDbFactory(final MongoClient client) {
        // Applications are expected to have one MongoDbFactory available
        return new SimpleMongoDbFactory(client, "standard");
    }

    @Bean
    public MongoTemplate mongoTemplate(final MongoDbFactory factory) {
        // Used in tests
        return new MongoTemplate(factory);
    }

    // A fake mongo for tests
    @Bean
    public Fongo fongo() {
        return new Fongo("MongoDB");
    }

    /**
     * @return A {@link TenantInformationProvider} that gets the database name from {@link #dataBaseName}.
     */
    @Bean
    public TenantInformationProvider tenantProvider() {
        return new TenantInformationProvider() {
            @Override
            public String getDatabaseName() {
                if("failure".equals(dataBaseName.get())) {
                    throw new RuntimeException("On purpose thrown exception.");
                }
                return dataBaseName.get();
            }

            @Override
            public UserCredentials getCredentials() {
                return new UserCredentials("", "");
            }

            @Override
            public String getAuthenticationDatabaseName() {
                if("failure".equals(dataBaseName.get())) {
                    throw new RuntimeException("On purpose thrown exception.");
                }
                return dataBaseName.get();
            }
        };
    }
}
