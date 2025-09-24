package com.example.franchise.application.usecase;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.FranchiseRepository;
import com.example.franchise.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RenameAndRemoveUseCasesTest {
    FranchiseRepository franchiseRepository;
    BranchRepository branchRepository;
    ProductRepository productRepository;

    RenameFranchiseUseCase renameFranchiseUseCase;
    RenameBranchUseCase renameBranchUseCase;
    RenameProductUseCase renameProductUseCase;
    RemoveProductFromBranchUseCase removeProductFromBranchUseCase;

    @BeforeEach
    void setup() {
        franchiseRepository = Mockito.mock(FranchiseRepository.class);
        branchRepository = Mockito.mock(BranchRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        renameFranchiseUseCase = new RenameFranchiseUseCase(franchiseRepository);
        renameBranchUseCase = new RenameBranchUseCase(branchRepository);
        renameProductUseCase = new RenameProductUseCase(productRepository);
        removeProductFromBranchUseCase = new RemoveProductFromBranchUseCase(productRepository);
    }

    @Test
    void renameFranchise() {
    when(franchiseRepository.findById("F1")).thenReturn(Mono.just(com.example.franchise.domain.model.Franchise.builder().id("F1").name("ACME").build()));
        when(franchiseRepository.existsByName("NEW")).thenReturn(Mono.just(false));
    when(franchiseRepository.rename("F1", "NEW")).thenReturn(Mono.just(com.example.franchise.domain.model.Franchise.builder().id("F1").name("NEW").build()));
        StepVerifier.create(renameFranchiseUseCase.execute("F1", "NEW")).verifyComplete();
    }

    @Test
    void renameBranch() {
        when(branchRepository.findById("B1")).thenReturn(Mono.just(Branch.builder().id("B1").franchiseId("F1").name("Old").build()));
        when(branchRepository.rename("B1", "New")).thenReturn(Mono.just(Branch.builder().id("B1").franchiseId("F1").name("New").build()));
        StepVerifier.create(renameBranchUseCase.execute("F1", "B1", "New")).verifyComplete();
    }

    @Test
    void renameProduct() {
        when(productRepository.findById("P1")).thenReturn(Mono.just(Product.builder().id("P1").branchId("B1").name("Old").stock(1).build()));
        when(productRepository.rename("P1", "New")).thenReturn(Mono.just(Product.builder().id("P1").branchId("B1").name("New").stock(1).build()));
        StepVerifier.create(renameProductUseCase.execute("F1", "B1", "P1", "New")).verifyComplete();
    }

    @Test
    void removeProduct() {
        when(productRepository.findById("P1")).thenReturn(Mono.just(Product.builder().id("P1").branchId("B1").name("X").stock(1).build()));
        when(productRepository.deleteById("P1")).thenReturn(Mono.empty());
        StepVerifier.create(removeProductFromBranchUseCase.execute("F1", "B1", "P1")).verifyComplete();
    }
}
