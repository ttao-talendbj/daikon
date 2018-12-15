/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.talend.daikon.spring.configuration.etcd;

import java.nio.charset.Charset;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableConfigurationProperties
public class EtcdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Client etcdClient(@Autowired EtcdProperties etcdProperties) throws SSLException {
        ClientBuilder builder = Client.builder();
        builder = builder.endpoints(etcdProperties.getUris().toArray(new String[0]));
        if (etcdProperties.getUser() != null) {
            builder = builder.user(ByteSequence.from(etcdProperties.getUser(), Charset.forName("UTF-8")));
        }
        if (etcdProperties.getPassword() != null) {
            builder = builder.password(ByteSequence.from(etcdProperties.getPassword(), Charset.forName("UTF-8")));
        }
        if (etcdProperties.isSslEnabled()) {
            builder = builder.sslContext(SslContextBuilder.forClient().build());
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public EtcdProperties etcdProperties() {
        return new EtcdProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public EtcdEndpoint etcdEndpoint(@Autowired Client client) {
        return new EtcdEndpoint(client);
    }

    @Bean
    @ConditionalOnMissingBean
    public EtcdHealthIndicator etcdHealthIndicator(@Autowired Client client) {
        return new EtcdHealthIndicator(client);
    }
}
