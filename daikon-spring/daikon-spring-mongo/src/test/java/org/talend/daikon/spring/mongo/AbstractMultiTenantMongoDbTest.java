package org.talend.daikon.spring.mongo;

import com.github.fakemongo.Fongo;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.talend.daikon.spring.mongo.TestMultiTenantConfiguration.changeTenant;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories
@ComponentScan("org.talend.daikon.spring.mongo")
@ContextConfiguration(classes = MultiTenantMongoDbFactoryTest.class)
public abstract class AbstractMultiTenantMongoDbTest {

    @Autowired
    private Fongo fongo;

    @Before
    public void tearDown() throws Exception {
        // Drop all created databases during test
        final List<String> databases = fongo.getDatabaseNames();
        for (String database : databases) {
            fongo.dropDatabase(database);
        }
        //
        changeTenant("default");
    }
}
