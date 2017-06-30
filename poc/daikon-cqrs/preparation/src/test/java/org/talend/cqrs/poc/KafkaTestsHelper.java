// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cqrs.poc;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class KafkaTestsHelper<K, V> {

    private final KafkaEmbedded kafkaEmbedded;

    private KafkaMessageListenerContainer<K, V> listenerContainer;

    private BlockingQueue<ConsumerRecord<K, V>> records;

    public KafkaTestsHelper(KafkaEmbedded kafkaEmbedded) {
        this.kafkaEmbedded = kafkaEmbedded;
    }

    public void startConsuming(String groupName, int partitionsPerTopic, String... topics) throws Exception {
        if (listenerContainer != null) {
            throw new IllegalStateException("Already consuming");
        }

        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(groupName, Boolean.FALSE.toString(),
                this.kafkaEmbedded);

        DefaultKafkaConsumerFactory<K, V> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);

        ContainerProperties containerProperties = new ContainerProperties(topics);

        listenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        records = new LinkedBlockingQueue<>();

        listenerContainer.setupMessageListener((MessageListener<K, V>) record -> records.add(record));

        listenerContainer.start();

        ContainerTestUtils.waitForAssignment(listenerContainer, partitionsPerTopic);

    }

    public void stopConsuming() {
        if (listenerContainer != null) {
            listenerContainer.stop();
            listenerContainer = null;
            records = null;
        }
    }

    public ConsumerRecord<K, V> pollRecord(long timeout, TimeUnit timeUnit) throws InterruptedException {
        if (records == null) {
            throw new IllegalStateException("Consumer is not started");
        }
        return records.poll(timeout, timeUnit);
    }
}
