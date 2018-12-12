package org.talend.spring.configuration.demo;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.daikon.spring.SensitiveValue;

@Component
public class MyComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyComponent.class);

    @SensitiveValue("${encryptedValue}")
    private String value;

    @PostConstruct
    public void init() {
        LOGGER.info("Value: {}", value);
    }
}
