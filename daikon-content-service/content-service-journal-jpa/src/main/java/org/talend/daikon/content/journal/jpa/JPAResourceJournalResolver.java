package org.talend.daikon.content.journal.jpa;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.content.journal.ResourceJournal;

@EnableJpaRepositories
public class JPAResourceJournalResolver implements ResourceJournal {

    static final String JOURNAL_READY = ".journal.ready";

    @Autowired
    private JPAResourceJournalRepository repository;

    @Override
    public void sync(ResourceResolver resourceResolver) {

    }

    @Override
    public Stream<String> matches(String pattern) {
        return repository.findByNameStartsWith(pattern).stream().map(ResourceJournalEntry::getName);
    }

    @Override
    public void clear(String pattern) {
        repository.deleteByNameStartsWith(pattern);
    }

    @Override
    public void add(String location) {
        repository.save(new ResourceJournalEntry(location));
    }

    @Override
    public void remove(String location) {
        repository.deleteByName(location);
    }

    @Override
    public void move(String source, String target) {
        repository.deleteByName(source);
        add(target);
    }

    @Override
    public boolean exist(String location) {
        return repository.countByName(location) > 0;
    }

    @Override
    public boolean ready() {
        return repository.exists(JOURNAL_READY);
    }

    @Override
    public void validate() {
        repository.save(new ResourceJournalEntry(JOURNAL_READY));
    }

    @Override
    public void invalidate() {
        repository.delete(new ResourceJournalEntry(JOURNAL_READY));
    }
}
