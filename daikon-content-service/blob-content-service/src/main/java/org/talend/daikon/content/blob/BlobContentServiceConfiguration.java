package org.talend.daikon.content.blob;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.content.ResourceResolver;

import com.microsoft.azure.storage.blob.CloudBlobClient;

@Configuration
@ConditionalOnProperty(name = "content-service.store", havingValue = "blob")
public class BlobContentServiceConfiguration {

    @Bean
    public ResourceResolver blobPathResolver(CloudBlobClient client) {
        return new BlobResourceResolver(client);
    }

}
