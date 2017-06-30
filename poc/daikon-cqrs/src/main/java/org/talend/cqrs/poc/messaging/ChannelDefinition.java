package org.talend.cqrs.poc.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * defines the spring-cloud-stream output channel to be used. see @EnableBinding in
 * {@link AxonEventsToSpringMessageAdapter}
 */
public interface ChannelDefinition {

    String PREPARATION_EVENTS = "preparationEvents";

    @Output(PREPARATION_EVENTS)
    MessageChannel preparationEvents();
}
