package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.*;
import com.example.franchise.domain.model.Product;
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

@WebFluxTest(controllers = {ProductController.class, GlobalErrorHandler.class})
@Import({GlobalErrorHandler.class, TestMetricsConfig.class, ProductControllerTest.Mocks.class})
class ProductControllerTest {

    @Autowired WebTestClient client;
    @Autowired AddProductToBranchUseCase addProductToBranchUseCase;
    @Autowired UpdateProductStockUseCase updateProductStockUseCase;
    @Autowired RenameProductUseCase renameProductUseCase;
    @Autowired RemoveProductFromBranchUseCase removeProductFromBranchUseCase;

    @Test
    void createProduct() {
        Mockito.when(addProductToBranchUseCase.execute("F1", "B1", "Widget", 5))
                .thenReturn(Mono.just(Product.builder().id("P1").branchId("B1").name("Widget").stock(5).build()));

        client.post().uri("/api/v1/franchises/{f}/branches/{b}/products", "F1", "B1")
                .header("Content-Type","application/json")
                .bodyValue("{\"name\":\"Widget\",\"stock\":5}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Widget")
                .jsonPath("$.stock").isEqualTo(5);
    }

    @Test
    void updateStock() {
        Mockito.when(updateProductStockUseCase.execute("F1", "B1", "P1", 9))
                .thenReturn(Mono.just(Product.builder().id("P1").branchId("B1").name("Widget").stock(9).build()));

        client.patch().uri("/api/v1/franchises/{f}/branches/{b}/products/{p}/stock", "F1", "B1", "P1")
                .header("Content-Type","application/json")
                .bodyValue("{\"stock\":9}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(9);
    }

    @Test
    void renameProduct() {
        Mockito.when(renameProductUseCase.execute("F1", "B1", "P1", "New"))
                .thenReturn(Mono.empty());

        client.patch().uri("/api/v1/franchises/{f}/branches/{b}/products/{p}", "F1", "B1", "P1")
                .header("Content-Type","application/json")
                .bodyValue("{\"name\":\"New\"}")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteProduct() {
        Mockito.when(removeProductFromBranchUseCase.execute("F1", "B1", "P1")).thenReturn(Mono.empty());

        client.delete().uri("/api/v1/franchises/{f}/branches/{b}/products/{p}", "F1", "B1", "P1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @TestConfiguration
    static class Mocks {
        @Bean AddProductToBranchUseCase addProductToBranchUseCase() { return Mockito.mock(AddProductToBranchUseCase.class); }
        @Bean UpdateProductStockUseCase updateProductStockUseCase() { return Mockito.mock(UpdateProductStockUseCase.class); }
        @Bean RenameProductUseCase renameProductUseCase() { return Mockito.mock(RenameProductUseCase.class); }
        @Bean RemoveProductFromBranchUseCase removeProductFromBranchUseCase() { return Mockito.mock(RemoveProductFromBranchUseCase.class); }
    }
}
