package org.talend.daikon.spring.mongo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * A very simple implementation of {@link MongoClientProvider}. This provider does not allow selected eviction of cached
 * instances.
 */
public class SimpleMongoClientProvider implements MongoClientProvider {

    private final Map<MongoClientURI, MongoClient> clients = new HashMap<>();

    @Override
    public MongoClient get(TenantInformationProvider provider) {
        final MongoClientURI databaseURI = provider.getDatabaseURI();
        clients.computeIfAbsent(databaseURI, uri -> {
            try {
                return new MongoClient(uri);
            } catch (UnknownHostException e) {
                throw new InvalidDataAccessResourceUsageException("Unable to retrieve host information.", e);
            }
        });
        return clients.get(databaseURI);
    }

    @Override
    public void close() throws IOException {
        for (Map.Entry<MongoClientURI, MongoClient> entry : clients.entrySet()) {
            entry.getValue().close();
        }
        clients.clear();
    }
}
