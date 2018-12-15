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

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;

/**
 * @author Spencer Gibb
 */
public class EtcdHealthIndicator extends AbstractHealthIndicator {

    private final Client client;

    public EtcdHealthIndicator(Client client) {
        this.client = client;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            client.getKVClient().get(ByteSequence.from("\0", Charset.defaultCharset())).get();
            builder.up();
        } catch (Exception e) {
            builder.down(e);
        }
    }

}
