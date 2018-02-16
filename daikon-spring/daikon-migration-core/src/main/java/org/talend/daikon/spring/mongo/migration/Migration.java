package org.talend.daikon.spring.mongo.migration;

import java.util.function.BiFunction;

/**
 * A way to declare a migration to <code>T</code> and uses <code>S</code>.
 *
 * @param <S> The source object that can be used to the <code>T</code> object.
 * @param <T> The application <code>T</code> object.
 */
public interface Migration<S, T> extends BiFunction<S, T, T> {

    T apply(S source, T target);

}
