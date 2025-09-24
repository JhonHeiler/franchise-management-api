package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.port.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateFranchiseUseCaseTest {
    FranchiseRepository repo;
    CreateFranchiseUseCase useCase;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(FranchiseRepository.class);
        useCase = new CreateFranchiseUseCase(repo);
    }

    @Test
    void createsFranchise() {
        when(repo.existsByName("X")).thenReturn(Mono.just(false));
        when(repo.save(any())).thenAnswer(inv -> Mono.just((Franchise) inv.getArgument(0)));
        StepVerifier.create(useCase.execute("X"))
                .assertNext(f -> {
                    assert f.getId() != null; assert f.getName().equals("X");
                })
                .verifyComplete();
    }

    @Test
    void duplicateName() {
        when(repo.existsByName("X")).thenReturn(Mono.just(true));
        StepVerifier.create(useCase.execute("X"))
                .expectError(DomainErrors.Conflict.class)
                .verify();
    }

    @Test
    void blankName() {
        StepVerifier.create(useCase.execute(" "))
                .expectError(DomainErrors.Validation.class)
                .verify();
    }
}
