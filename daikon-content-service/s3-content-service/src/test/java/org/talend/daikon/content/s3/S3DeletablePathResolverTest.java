package org.talend.daikon.content.s3;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.talend.daikon.content.DeletableResourceLoaderTest;

@ContextConfiguration(classes = { S3ContentServiceConfiguration.class, TestConfiguration.class })
@TestPropertySource(properties = { "content-service.store=s3", "content-service.store.s3.authentication=custom",
        "multi-tenancy.s3.active=true" })
public class S3DeletablePathResolverTest extends DeletableResourceLoaderTest {
    // All standard test
}
