package org.talend.daikon.content.journal.jpa;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.repository.CrudRepository;

public interface JPAResourceJournalRepository extends CrudRepository<ResourceJournalEntry, String> {

    List<ResourceJournalEntry> findByNameStartsWith(String name);

    long countByName(String name);

    void deleteByName(String name);

    void deleteByNameStartsWith(String name);

    ResourceJournalEntry save(ResourceJournalEntry resourceJournalEntry);

    ResourceJournalEntry findOne(Example<ResourceJournalEntry> of);

    boolean exists(String journalReadyMarker);
}