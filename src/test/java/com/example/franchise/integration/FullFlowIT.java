package com.example.franchise.integration;

import com.example.franchise.FranchiseApiApplication;
import com.example.franchise.infrastructure.adapter.repository.mongo.document.BranchDocument;
import com.example.franchise.infrastructure.adapter.repository.mongo.document.FranchiseDocument;
import com.example.franchise.infrastructure.adapter.repository.mongo.document.ProductDocument;
import com.example.franchise.testsupport.MongoContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootTest(classes = FranchiseApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "testcontainers.enabled=true")
@Import(MongoContainerConfig.class)
@ActiveProfiles("test")
@org.junit.jupiter.api.Tag("integration")
class FullFlowIT {

    @Autowired WebTestClient client;
    @Autowired ReactiveMongoTemplate template;

    @Test
    void aggregationReturnsTopProductPerBranch() {
        String franchiseId = UUID.randomUUID().toString();
        String branchId = UUID.randomUUID().toString();
        String productId = UUID.randomUUID().toString();

        Mono.when(
                template.insert(new FranchiseDocument(franchiseId, "ACME"), "franchises"),
                template.insert(new BranchDocument(branchId, franchiseId, "Central"), "branches"),
                template.insert(new ProductDocument(productId, branchId, "Widget", 42), "products"),
                template.insert(new ProductDocument(UUID.randomUUID().toString(), branchId, "Widget2", 10), "products")
        ).block();

        client.get().uri("/api/v1/franchises/{id}/branches/max-stock-products", franchiseId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].productName").isEqualTo("Widget")
                .jsonPath("$[0].stock").isEqualTo(42);
    }
}
