package org.talend.daikon.spring.mongo;

import java.io.Closeable;

import com.mongodb.MongoClient;

/**
 * Implement this interface to obtain a {@link MongoClient client} for a tenant (with information available through
 * {@link TenantInformationProvider}).
 */
public interface MongoClientProvider extends Closeable {

    /**
     * <p>
     * Obtain a {@link MongoClient} for given tenant. Implementations are encouraged to perform cache of clients and reuse
     * previously
     * created client.
     * </p>
     * <p>
     * Cache <b>must</b> be cleared upon {@link Closeable#close()} call.
     * </p>
     *
     * @param provider An implementation of {@link TenantInformationProvider} to be used to get tenant information (database uri,
     * database name).
     * @return A {@link MongoClient client} to access tenant MongoDB.
     */
    MongoClient get(TenantInformationProvider provider);

}
