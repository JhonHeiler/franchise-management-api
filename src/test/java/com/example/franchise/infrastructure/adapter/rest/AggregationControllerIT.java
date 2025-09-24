package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.FranchiseApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = FranchiseApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "testcontainers.enabled=true")
@ActiveProfiles("test")
@org.junit.jupiter.api.Tag("integration")
class AggregationControllerIT {

    @Autowired
    WebTestClient client;

    @Test
    void emptyAggregationReturnsNotFound() {
        client.get().uri("/api/v1/franchises/any/branches/max-stock-products")
                .exchange()
                .expectStatus().isNotFound();
    }
}
