package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateProductStockUseCaseTest {
    ProductRepository productRepository;
    UpdateProductStockUseCase useCase;

    @BeforeEach
    void setup() {
        productRepository = Mockito.mock(ProductRepository.class);
        useCase = new UpdateProductStockUseCase(productRepository);
    }

    @Test
    void updatesStock() {
        when(productRepository.findById("P1")).thenReturn(Mono.just(Product.builder().id("P1").branchId("B1").name("Cafe").stock(1).build()));
        when(productRepository.updateStock(eq("P1"), anyInt())).thenAnswer(inv -> Mono.just(Product.builder().id("P1").branchId("B1").name("Cafe").stock((Integer) inv.getArgument(1)).build()));

    StepVerifier.create(useCase.execute("F1", "B1", "P1", 5))
        .assertNext(p -> org.junit.jupiter.api.Assertions.assertEquals(5, p.getStock()))
        .verifyComplete();
    }

    @Test
    void rejectsNegative() {
        StepVerifier.create(useCase.execute("F1", "B1", "P1", -2))
                .expectError(DomainErrors.Validation.class)
                .verify();
    }
}
