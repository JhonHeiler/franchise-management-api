package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.GetFranchiseWithBranchesUseCase;
import com.example.franchise.application.usecase.GetFranchiseWithBranchesUseCase.FranchiseAggregate;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.testsupport.TestMetricsConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(controllers = {FranchiseQueryController.class, GlobalErrorHandler.class})
@Import({GlobalErrorHandler.class, TestMetricsConfig.class, FranchiseQueryControllerTest.Mocks.class})
class FranchiseQueryControllerTest {

    @Autowired WebTestClient client;
        @Autowired GetFranchiseWithBranchesUseCase getFranchiseWithBranchesUseCase;

    @Test
    void returnsAggregate() {
        Franchise f = Franchise.builder().id("F1").name("ACME").build();
        List<Branch> branches = List.of(
                Branch.builder().id("B1").franchiseId("F1").name("Central").build()
        );
        Mockito.when(getFranchiseWithBranchesUseCase.execute("F1"))
                .thenReturn(Mono.just(new FranchiseAggregate(f, branches)));

        client.get().uri("/api/v1/franchises/{id}/with-branches", "F1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.franchise.name").isEqualTo("ACME")
                .jsonPath("$.branches[0].name").isEqualTo("Central");
    }

        @TestConfiguration
        static class Mocks {
                @Bean
                GetFranchiseWithBranchesUseCase getFranchiseWithBranchesUseCase() {
                        return Mockito.mock(GetFranchiseWithBranchesUseCase.class);
                }
        }
}
