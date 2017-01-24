package org.talend.daikon.spring.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoDbUtils;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.util.Assert;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * A {@link SimpleMongoDbFactory} that allows external code to choose which MongoDB database should be accessed.
 *
 * @see TenantInformationProvider
 */
class MultiTenancyMongoDbFactory implements MongoDbFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiTenancyMongoDbFactory.class);

    private final MongoDbFactory delegate;

    private final TenantInformationProvider tenantProvider;

    private final MongoClient mongoClient;

    MultiTenancyMongoDbFactory(final MongoDbFactory delegate, final TenantInformationProvider tenantProvider,
            final MongoClient mongoClient) {
        this.delegate = delegate;
        this.tenantProvider = tenantProvider;
        this.mongoClient = mongoClient;
    }

    @Override
    public DB getDb() {
        // Multi tenancy database name selection
        final String databaseName;
        final UserCredentials credentials;
        final String authenticationDatabase;
        try {
            databaseName = tenantProvider.getDatabaseName();
            credentials = tenantProvider.getCredentials();
            authenticationDatabase = tenantProvider.getAuthenticationDatabaseName();
        } catch (Exception e) {
            throw new InvalidDataAccessResourceUsageException("Unable to retrieve tenant information.", e);
        }
        Assert.hasText(databaseName, "Database name must not be empty.");
        LOGGER.debug("Using '{}' as Mongo database.", databaseName);

        // Get MongoDB database using tenant information
        // Using deprecated method as it's the only one that allows override of mongo client's.
        DB db = MongoDbUtils.getDB(mongoClient, databaseName, credentials, authenticationDatabase);

        // Get WriteConcern from mongo client
        if (mongoClient.getWriteConcern() != null) {
            db.setWriteConcern(mongoClient.getWriteConcern());
        }

        // All clear: return database
        return db;
    }

    @Override
    public DB getDb(String dbName) {
        // There's no reason the database name parameter should be considered here (information belongs to the tenant).
        return getDb();
    }

    @Override
    public PersistenceExceptionTranslator getExceptionTranslator() {
        return delegate.getExceptionTranslator();
    }

}
