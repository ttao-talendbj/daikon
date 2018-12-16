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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.EnumerablePropertySource;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;

/**
 * @author Luca Burgazzoli
 * @author Spencer Gibb
 */
public class EtcdPropertySource extends EnumerablePropertySource<Client> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdPropertySource.class);

    private final Map<String, String> properties = new HashMap<>();

    private final ApplicationContext context;

    public EtcdPropertySource(String root, Client source, ApplicationContext context) {
        super(root, source);
        this.context = context;
    }

    public void init() {
        try {
            final Client client = getSource();

            // Init KV
            final KV kv = client.getKVClient();
            final ByteSequence key = ByteSequence.from(getName(), Charset.forName("UTF-8"));
            final GetOption option = GetOption.newBuilder() //
                    .withSortField(GetOption.SortTarget.KEY) //
                    .withSortOrder(GetOption.SortOrder.DESCEND) //
                    .withPrefix(key) //
                    .build();
            final GetResponse response = kv.get(key, option).get();
            process(response);

            // Init watch client
            if (Optional.ofNullable(context).isPresent()) {
                final WatchOption watchOption = WatchOption.newBuilder().withPrefix(key).build();
                final Watch watchClient = client.getWatchClient();
                watchClient.watch(key, watchOption, new ChangeListener());
            } else {
                LOGGER.warn("No configuration reload capabilities (no application context).");
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to init properties.", e);
        }
    }

    @Override
    public String[] getPropertyNames() {
        return properties.keySet().toArray(new String[0]);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    private void process(GetResponse response) {
        final List<KeyValue> values = response.getKvs();
        for (KeyValue value : values) {
            process(value);
        }
    }

    private void process(KeyValue value) {
        final String key = extractKey(value);
        LOGGER.debug("Added key '{}'.", key);
        properties.put(key, value.getValue().toString(Charset.forName("UTF-8")));
    }

    private void delete(KeyValue value) {
        final String key = extractKey(value);
        LOGGER.debug("Removed key '{}'.", key);
        properties.remove(key);
    }

    private String extractKey(KeyValue value) {
        String keyAsString = value.getKey().toString(Charset.forName("UTF-8")).substring(getName().length());
        if (keyAsString.startsWith(EtcdConstants.PATH_SEPARATOR)) {
            keyAsString = keyAsString.substring(1);
        }
        return keyAsString.replace(EtcdConstants.PATH_SEPARATOR, EtcdConstants.PROPERTIES_SEPARATOR);
    }

    private class ChangeListener implements Watch.Listener {

        @Override
        public void onNext(WatchResponse watchResponse) {
            final List<WatchEvent> events = watchResponse.getEvents();
            final Set<String> modifiedKeys = events.stream() //
                    .peek(e -> {
                        if (e.getEventType() == WatchEvent.EventType.PUT) {
                            process(e.getKeyValue());
                        } else {
                            delete(e.getKeyValue());
                        }
                    }) //
                    .map(e -> extractKey(e.getKeyValue())) //
                    .collect(Collectors.toSet());

            if (!modifiedKeys.isEmpty()) {
                LOGGER.info("Refreshing application context...");
                context.publishEvent(new EnvironmentChangeEvent(modifiedKeys));
                LOGGER.info("Application context refreshed.");
            }
        }

        @Override
        public void onError(Throwable throwable) {
            LOGGER.warn("Watcher met an issue: {}", throwable);
        }

        @Override
        public void onCompleted() {
            LOGGER.info("Watcher ended.");
        }
    }
}
