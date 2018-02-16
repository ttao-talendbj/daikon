package org.talend.daikon.spring.mongo.migration;

import java.util.function.Function;
import java.util.stream.Stream;

public interface MigrationSource extends Function<Class<?>, Stream<Class<?>>> {
}
