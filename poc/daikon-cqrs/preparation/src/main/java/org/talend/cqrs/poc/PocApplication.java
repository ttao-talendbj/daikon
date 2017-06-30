package org.talend.cqrs.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.talend.daikon.events.spring.EnableEventsMetadata;

@SpringBootApplication
@EnableEventsMetadata
public class PocApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocApplication.class, args);
    }

}
