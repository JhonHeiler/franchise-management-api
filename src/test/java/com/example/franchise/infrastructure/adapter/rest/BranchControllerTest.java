package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.AddBranchToFranchiseUseCase;
import com.example.franchise.application.usecase.RenameBranchUseCase;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.testsupport.TestMetricsConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = {BranchController.class, GlobalErrorHandler.class})
@Import({GlobalErrorHandler.class, TestMetricsConfig.class, BranchControllerTest.Mocks.class})
class BranchControllerTest {

    @Autowired WebTestClient client;
    @Autowired AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    @Autowired RenameBranchUseCase renameBranchUseCase;

    @Test
    void createBranch() {
        Mockito.when(addBranchToFranchiseUseCase.execute("F1", "Central")).thenReturn(Mono.just(
                Branch.builder().id("B1").franchiseId("F1").name("Central").build()
        ));

        client.post().uri("/api/v1/franchises/{id}/branches", "F1")
                .header("Content-Type","application/json")
                .bodyValue("{\"name\":\"Central\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Central");
    }

    @Test
    void renameBranch() {
        Mockito.when(renameBranchUseCase.execute("F1", "B1", "New")).thenReturn(Mono.empty());

        client.patch().uri("/api/v1/franchises/{f}/branches/{b}", "F1", "B1")
                .header("Content-Type","application/json")
                .bodyValue("{\"name\":\"New\"}")
                .exchange()
                .expectStatus().isNoContent();
    }

    @TestConfiguration
    static class Mocks {
        @Bean AddBranchToFranchiseUseCase addBranchToFranchiseUseCase() { return Mockito.mock(AddBranchToFranchiseUseCase.class); }
        @Bean RenameBranchUseCase renameBranchUseCase() { return Mockito.mock(RenameBranchUseCase.class); }
    }
}
