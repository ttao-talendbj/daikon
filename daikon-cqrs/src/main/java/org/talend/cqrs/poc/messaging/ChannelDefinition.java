package org.talend.cqrs.poc.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ChannelDefinition {

    @Output("output")
    MessageChannel output();
}
