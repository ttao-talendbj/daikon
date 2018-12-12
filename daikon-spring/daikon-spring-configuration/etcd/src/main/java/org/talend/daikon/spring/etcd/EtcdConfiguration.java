package org.talend.daikon.spring.etcd;

import java.net.URI;
import java.nio.charset.Charset;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.netty.handler.ssl.SslContextBuilder;

/**
 *
 */
@Configuration
@ConditionalOnBean(EtcdProperties.class)
public class EtcdConfiguration {

    @Value("${spring.application.name:app}")
    private String applicationName;

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
    public Client etcdClient(@Autowired EtcdProperties etcdProperties) throws SSLException {
        ClientBuilder builder = Client.builder();
        builder = builder.endpoints(etcdProperties.getUris().toArray(new URI[0]));
        if (etcdProperties.getPassword() != null) {
            builder = builder.password(ByteSequence.from(etcdProperties.getPassword(), Charset.forName("UTF-8")));
        }
        if (etcdProperties.isSslEnabled()) {
            builder = builder.sslContext(SslContextBuilder.forClient().build());
        }
        return builder.build();
    }

    @Bean
    public EtcdPropertySource etcdPropertySource(Client client) {
        final EtcdPropertySource propertySource = new EtcdPropertySource("etcd", applicationName, client);
        final MutablePropertySources sources = environment.getPropertySources();
        sources.addLast(propertySource);
        return propertySource;
    }
}
