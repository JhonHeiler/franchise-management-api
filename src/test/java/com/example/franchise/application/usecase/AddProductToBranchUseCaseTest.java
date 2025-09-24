package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AddProductToBranchUseCaseTest {
    BranchRepository branchRepository;
    ProductRepository productRepository;
    AddProductToBranchUseCase useCase;

    @BeforeEach
    void setup() {
        branchRepository = Mockito.mock(BranchRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        useCase = new AddProductToBranchUseCase(branchRepository, productRepository);
    }

    @Test
    void addsProduct() {
        when(branchRepository.findById("B1")).thenReturn(Mono.just(Branch.builder().id("B1").franchiseId("F1").name("Centro").build()));
        when(productRepository.existsByBranchIdAndName("B1", "Cafe")).thenReturn(Mono.just(false));
        when(productRepository.save(any())).thenAnswer(inv -> Mono.just((Product) inv.getArgument(0)));

        StepVerifier.create(useCase.execute("F1", "B1", "Cafe", 10))
                .assertNext(p -> {
                    assert p.getId() != null; assert p.getBranchId().equals("B1"); assert p.getName().equals("Cafe"); assert p.getStock() == 10;
                })
                .verifyComplete();
    }

    @Test
    void rejectsNegativeStock() {
        StepVerifier.create(useCase.execute("F1", "B1", "Cafe", -1))
                .expectError(DomainErrors.Validation.class)
                .verify();
    }
}
