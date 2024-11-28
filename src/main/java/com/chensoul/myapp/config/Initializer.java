package com.chensoul.myapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class Initializer implements CommandLineRunner {
    private final Environment environment;

    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent event) {
        log.info("Application started at http://localhost:{}{}",
                environment.getProperty("server.port", "8080"),
                StringUtils.defaultString(environment.getProperty("server.servlet.context-path")));
    }

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
    }
}
