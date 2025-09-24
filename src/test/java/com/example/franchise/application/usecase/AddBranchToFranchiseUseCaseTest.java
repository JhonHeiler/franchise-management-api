package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AddBranchToFranchiseUseCaseTest {
    FranchiseRepository franchiseRepository;
    BranchRepository branchRepository;
    AddBranchToFranchiseUseCase useCase;

    @BeforeEach
    void setup() {
        franchiseRepository = Mockito.mock(FranchiseRepository.class);
        branchRepository = Mockito.mock(BranchRepository.class);
        useCase = new AddBranchToFranchiseUseCase(franchiseRepository, branchRepository);
    }

    @Test
    void addsBranch() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(Franchise.builder().id("F1").name("ACME").build()));
        when(branchRepository.existsByFranchiseIdAndName("F1", "Centro")).thenReturn(Mono.just(false));
        when(branchRepository.save(any())).thenAnswer(inv -> Mono.just((Branch) inv.getArgument(0)));

        StepVerifier.create(useCase.execute("F1", "Centro"))
                .assertNext(b -> {
                    assert b.getId() != null; assert b.getFranchiseId().equals("F1"); assert b.getName().equals("Centro");
                })
                .verifyComplete();
    }

    @Test
    void rejectsBlankName() {
        StepVerifier.create(useCase.execute("F1", " "))
                .expectError(DomainErrors.Validation.class)
                .verify();
    }
}
