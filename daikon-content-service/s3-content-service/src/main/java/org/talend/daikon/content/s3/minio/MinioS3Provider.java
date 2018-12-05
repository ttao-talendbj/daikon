package org.talend.daikon.content.s3.minio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.daikon.content.s3.provider.AmazonS3Provider;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Component
@ConditionalOnProperty(name = "content-service.store.s3.authentication", value = "MINIO")
public class MinioS3Provider implements AmazonS3Provider {

    @Value("${content-service.store.s3.minio.account_name}")
    private String accountName;

    @Value("${content-service.store.s3.minio.account_key}")
    private String accountKey;

    @Value("${content-service.store.s3.minio.endpoint_url}")
    private String endpointUrl;

    @Value("${content-service.store.s3.minio.region:us-east-1}")
    private String region;

    @Override
    public AmazonS3 getAmazonS3Client() {
        final AWSCredentials credentials = new BasicAWSCredentials(accountName, accountKey);
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");

        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl, region))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
