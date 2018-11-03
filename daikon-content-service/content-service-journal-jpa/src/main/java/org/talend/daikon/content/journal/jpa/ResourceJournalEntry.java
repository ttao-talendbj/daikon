package org.talend.daikon.content.journal.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A journal entry for {@link JPAResourceJournalResolver}.
 */
@Entity
public class ResourceJournalEntry {

    @Id
    private String name;

    public ResourceJournalEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}