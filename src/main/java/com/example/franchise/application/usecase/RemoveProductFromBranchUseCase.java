package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RemoveProductFromBranchUseCase {
    private static final Logger log = LoggerFactory.getLogger(RemoveProductFromBranchUseCase.class);
    private final ProductRepository productRepository;

    public Mono<Void> execute(String franchiseId, String branchId, String productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Product not found")))
                .filter(p -> p.getBranchId().equals(branchId))
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Product not found in branch")))
                .flatMap(p -> productRepository.deleteById(productId))
                .doOnSuccess(v -> log.info("Deleted product {} from branch {}", productId, branchId))
                .doOnError(e -> log.error("Error deleting product", e));
    }
}
