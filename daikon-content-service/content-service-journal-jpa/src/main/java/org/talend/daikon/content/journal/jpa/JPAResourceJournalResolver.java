package org.talend.daikon.content.journal.jpa;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;
import org.talend.daikon.content.journal.ResourceJournal;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

@Component
@EnableJpaRepositories
public class JPAResourceJournalResolver implements ResourceJournal {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAResourceJournalResolver.class);

    static final String JOURNAL_READY = ".journal.ready";

    @Autowired
    private JPAResourceJournalRepository repository;

    @Override
    public void sync(ResourceResolver resourceResolver) {
        if (ready()) {
            LOGGER.warn("Journal is flagged 'ready', consider calling invalidate() first.");
            return;
        }

        try {
            LOGGER.info("Running initial sync...");
            final DeletableResource[] resources = resourceResolver.getResources("/**");
            for (int i = 0; i < resources.length; i++) {
                add(resources[i].getFilename());
                if (i % 500 == 0) {
                    LOGGER.info("Sync in progress ({}/{})", i, resources.length);
                }
            }
            validate();
            LOGGER.info("Initial sync done.");
        } catch (IOException e) {
            invalidate();
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    public Stream<String> matches(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return Stream.empty();
        }
        final String patternForJournal = StringUtils.substringBeforeLast(pattern, "*");

        return repository.findByNameStartsWith(patternForJournal).stream().map(ResourceJournalEntry::getName);
    }

    @Override
    public void clear(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return;
        }
        if (!pattern.startsWith("/")) {
            pattern = "/" + pattern;
        }

        final String patternForJournal = StringUtils.substringBefore(pattern, "*");
        repository.deleteByNameStartsWith(patternForJournal);
    }

    @Override
    public void add(String location) {
        if (location == null) {
            return;
        }
        String savedLocation = location;
        if (!location.startsWith("/")) {
            savedLocation = "/" + location;
        }
        repository.save(new ResourceJournalEntry(savedLocation));
    }

    @Override
    public void remove(String location) {
        repository.deleteByName(location);
    }

    @Override
    public void move(String source, String target) {
        if (!exist(source)) {
            return;
        }

        repository.deleteByName(source);
        add(target);
    }

    @Override
    public boolean exist(String location) {
        String savedLocation = location;
        if (!location.startsWith("/")) {
            savedLocation = "/" + location;
        }
        return repository.existsById(savedLocation);
    }

    @Override
    public boolean ready() {
        return repository.existsById(JOURNAL_READY);
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
