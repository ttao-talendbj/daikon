package org.talend.daikon.content.journal.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.content.journal.AbstractResourceJournalResolverTest;

@RunWith(SpringRunner.class)
@ContextConfiguration
@DataJpaTest
public class JPAResourceJournalResolverTest extends AbstractResourceJournalResolverTest {

    @Autowired
    private JPAResourceJournalRepository repository;

    @Test
    public void testContext() {
        super.testContext();
        assertNotNull(repository);
    }

    @Test
    public void shouldInvalidateDeleteMarkDocument() {
        // When
        resolver.validate();

        // Then
        assertTrue(repository.existsById(JPAResourceJournalResolver.JOURNAL_READY));

        // When
        resolver.invalidate();

        // Then
        assertFalse(repository.existsById(JPAResourceJournalResolver.JOURNAL_READY));
    }

    @Configuration
    @ComponentScan("org.talend.daikon.content.journal")
    @AutoConfigurationPackage
    public static class SpringConfig {
    }

}