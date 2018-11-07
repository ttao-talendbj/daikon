package org.talend.daikon.content.journal.mongodb;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.content.journal.AbstractResourceJournalResolverTest;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;

@RunWith(SpringRunner.class)
@ContextConfiguration
@DataMongoTest
public class MongoResourceJournalResolverTest extends AbstractResourceJournalResolverTest {

    @Autowired
    private MongoResourceJournalRepository repository;

    @Test
    public void testContext() {
        super.testContext();
        assertNotNull(repository);
    }

    @Configuration
    @ComponentScan("org.talend.daikon.content.journal")
    public static class SpringConfig {

        @Bean
        public MongoClient fongo() {
            return new Fongo("resourceJournal").getMongo();
        }

    }

}
