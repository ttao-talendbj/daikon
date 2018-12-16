package org.talend.daikon.spring.mongo.migration;

import java.util.function.Function;

import com.mongodb.DBObject;

/**
 * A {@link Migration} that uses {@link DBObject} as source.
 *
 * @param <T> The POJO to be built out of the {@link DBObject}
 */
public interface MongoDBMigration<T> extends Migration<DBObject, T> {

    Function<DBObject, String> MONGODB_VERSION = dbo -> dbo.containsField("_version") ? String.valueOf(dbo.get("_version"))
            : null;
}
