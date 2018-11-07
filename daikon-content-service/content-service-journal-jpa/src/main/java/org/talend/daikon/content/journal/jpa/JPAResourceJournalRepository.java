package org.talend.daikon.content.journal.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAResourceJournalRepository extends JpaRepository<ResourceJournalEntry, String> {

    List<ResourceJournalEntry> findByNameStartsWith(String name);

    long countByName(String name);

    void deleteByName(String name);

    void deleteByNameStartsWith(String name);

}