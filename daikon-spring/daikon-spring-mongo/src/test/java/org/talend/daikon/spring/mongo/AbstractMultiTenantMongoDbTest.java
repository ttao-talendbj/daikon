package org.talend.daikon.spring.mongo;

import static org.talend.daikon.spring.mongo.TestMultiTenantConfiguration.changeTenant;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.fakemongo.Fongo;

@RunWith(SpringRunner.class)
@DataMongoTest
@ComponentScan("org.talend.daikon.spring.mongo")
@ContextConfiguration(classes = TestMultiTenantConfiguration.class)
public abstract class AbstractMultiTenantMongoDbTest {

    @Autowired
    private Fongo fongo;

    @Autowired
    private MongoClientProvider mongoClientProvider;

    @Autowired
    private TenantInformationProvider tenantInformationProvider;

    @Before
    public void tearDown() throws IOException {
        // Drop all created databases during test
        final List<String> databases = fongo.getDatabaseNames();
        for (String database : databases) {
            fongo.dropDatabase(database);
        }
        // Switch back to default tenant
        changeTenant("default");
        // Clean up mongodb clients
        mongoClientProvider.close();
    }
}
