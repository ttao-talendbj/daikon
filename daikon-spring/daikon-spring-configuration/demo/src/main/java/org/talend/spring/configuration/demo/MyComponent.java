package org.talend.spring.configuration.demo;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyComponent.class);

    @Value("${display.name}")
    private String value;

    @PostConstruct
    public void init() {
        LOGGER.info("Display name: {}", value);
    }
}
