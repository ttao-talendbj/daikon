package org.talend.daikon.spring.mongo.migration;

import java.util.List;

class CompositeMigration<S, T> implements Migration<S, T> {

    private final List<Migration<S, T>> migrations;

    CompositeMigration(List<Migration<S, T>> migrations) {
        this.migrations = migrations;
    }

    @Override
    public T apply(S source, T target) {
        T current = target;
        for (Migration<S, T> migration : migrations) {
            current = migration.apply(source, current);
        }
        return current;
    }

    public String toString() {
        return "CompositeMigration{" + "migrations=" + migrations + '}';
    }
}
