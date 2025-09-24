package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class GetMaxStockProductPerBranchUseCaseTest {

    ReactiveMongoTemplate template;
    GetMaxStockProductPerBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        template = Mockito.mock(ReactiveMongoTemplate.class);
        useCase = new GetMaxStockProductPerBranchUseCase(template);
    }

    @Test
    void returnsAggregationResults() {
        GetMaxStockProductPerBranchUseCase.BranchMaxStock row = new GetMaxStockProductPerBranchUseCase.BranchMaxStock(
                "B1", "Central", "P1", "Widget", 42
        );

        Mockito.when(template.aggregate(any(Aggregation.class), eq("branches"), eq(GetMaxStockProductPerBranchUseCase.BranchMaxStock.class)))
                .thenReturn(Flux.just(row));

        StepVerifier.create(useCase.execute("F1"))
                .expectNextMatches(r -> r.productName().equals("Widget") && r.stock() == 42)
                .verifyComplete();
    }

    @Test
    void emitsNotFoundWhenEmpty() {
        Mockito.when(template.aggregate(any(Aggregation.class), eq("branches"), eq(GetMaxStockProductPerBranchUseCase.BranchMaxStock.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute("F1"))
                .expectError(DomainErrors.NotFound.class)
                .verify();
    }
}
