package org.talend.daikon.spring.etcd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import io.etcd.jetcd.Client;

/**
 *
 */
@Configuration
@ConditionalOnProperty("etcd.url")
public class EtcdConfiguration {

    @Value("${spring.application.name:app}")
    private String applicationName;

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
    public Client etcdClient() {
        return Client.builder().endpoints(environment.getRequiredProperty("etcd.url")).build();
    }

    @Bean
    @ConditionalOnBean(Client.class)
    public EtcdPropertySource etcdPropertySource(Client client) {
        final EtcdPropertySource propertySource = new EtcdPropertySource("etcd", applicationName, client);
        final MutablePropertySources sources = environment.getPropertySources();
        sources.addLast(propertySource);
        return propertySource;
    }
}
