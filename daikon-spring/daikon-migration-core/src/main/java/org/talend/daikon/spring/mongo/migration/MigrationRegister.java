package org.talend.daikon.spring.mongo.migration;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A registration class for {@link Migration migrations}.
 */
public class MigrationRegister {

    private static final Map<Class<?>, List<Class<?>>> registrations = Collections.synchronizedMap(new HashMap<>());

    /**
     * @return a {@link MigrationSource} implementation that looks up in registered migrations.
     */
    public static MigrationSource registeredExtractors() {
        return t -> registrations.getOrDefault(t, emptyList()).stream().filter(Objects::nonNull);
    }

    /**
     * Register a {@link Migration migration} for a given <code>clazz</code>
     * 
     * @param clazz The class to register for migration
     * @param migration The {@link Migration} implementation
     * @param <T> The type of the class registered for migration.
     */
    public static <T> void register(Class<T> clazz, Class<? extends Migration<?, T>> migration) {
        if (registrations.get(clazz) == null) {
            final List<Class<?>> registrations = new ArrayList<>();
            registrations.add(migration);
            MigrationRegister.registrations.put(clazz, registrations);
        } else {
            registrations.get(clazz).add(migration);
        }
    }
}
