package com.example.franchise.application.usecase;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GetFranchiseWithBranchesUseCaseTest {

    FranchiseRepository franchiseRepository;
    BranchRepository branchRepository;
    GetFranchiseWithBranchesUseCase useCase;

    @BeforeEach
    void setUp() {
        franchiseRepository = Mockito.mock(FranchiseRepository.class);
        branchRepository = Mockito.mock(BranchRepository.class);
        useCase = new GetFranchiseWithBranchesUseCase(franchiseRepository, branchRepository);
    }

    @Test
    void combineFranchiseAndBranches() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(Franchise.builder().id("F1").name("ACME").build()));
        when(branchRepository.findByFranchiseId("F1")).thenReturn(Flux.fromIterable(List.of(
                Branch.builder().id("B1").franchiseId("F1").name("Central").build(),
                Branch.builder().id("B2").franchiseId("F1").name("North").build()
        )));

        StepVerifier.create(useCase.execute("F1"))
                .assertNext(agg -> {
                    org.junit.jupiter.api.Assertions.assertEquals("ACME", agg.franchise().getName());
                    org.junit.jupiter.api.Assertions.assertEquals(2, agg.branches().size());
                })
                .verifyComplete();
    }

    @Test
    void emptyWhenFranchiseNotFound() {
        when(franchiseRepository.findById(anyString())).thenReturn(Mono.empty());
        when(branchRepository.findByFranchiseId(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute("missing"))
                .verifyComplete();
    }
}
