package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.CreateFranchiseUseCase;
import com.example.franchise.application.usecase.RenameFranchiseUseCase;
import com.example.franchise.domain.model.Franchise;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.example.franchise.testsupport.TestMetricsConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = {FranchiseController.class, GlobalErrorHandler.class})
@Import({GlobalErrorHandler.class, TestMetricsConfig.class})
class FranchiseControllerTest {

    @Autowired
    WebTestClient client;

    @MockBean
    CreateFranchiseUseCase createFranchiseUseCase;

    @MockBean
    RenameFranchiseUseCase renameFranchiseUseCase;

    @Test
    void createFranchise() {
        Mockito.when(createFranchiseUseCase.execute("ACME")).thenReturn(Mono.just(Franchise.builder().id("1").name("ACME").build()));
        client.post().uri("/api/v1/franchises").bodyValue("{\"name\":\"ACME\"}")
                .header("Content-Type","application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.name").isEqualTo("ACME");
    }
}
