package com.example.franchise.testsupport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Profile("test")
@ConditionalOnProperty(name = "testcontainers.enabled", havingValue = "true")
public class MongoContainerConfig {
    private static final MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:7"));

    static {
        mongo.start();
    }

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Bean
    public MongoDBContainer mongoDBContainer() { return mongo; }
}
