package org.talend.daikon.content.journal.jpa;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.content.journal.ResourceJournal;

@EnableJpaRepositories
public class JPAResourceJournalResolver implements ResourceJournal {

    @Override
    public void sync(ResourceResolver resourceResolver) {

    }

    @Override
    public Stream<String> matches(String pattern) {
        return null;
    }

    @Override
    public void clear(String pattern) {

    }

    @Override
    public void add(String location) {

    }

    @Override
    public void remove(String location) {

    }

    @Override
    public void move(String source, String target) {

    }

    @Override
    public boolean exist(String location) {
        return false;
    }

    @Override
    public boolean ready() {
        return false;
    }

    @Override
    public void validate() {

    }

    @Override
    public void invalidate() {

    }
}
