package com.example.franchise.testsupport;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestMetricsConfig {
    @Bean
    public SimpleMeterRegistry simpleMeterRegistry() { return new SimpleMeterRegistry(); }
}
