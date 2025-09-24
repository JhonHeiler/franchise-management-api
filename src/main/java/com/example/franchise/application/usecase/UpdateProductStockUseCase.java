package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UpdateProductStockUseCase {
    private static final Logger log = LoggerFactory.getLogger(UpdateProductStockUseCase.class);
    private final ProductRepository productRepository;

    public Mono<com.example.franchise.domain.model.Product> execute(String franchiseId, String branchId, String productId, int newStock) {
        if (newStock < 0) return Mono.error(new DomainErrors.Validation("Stock must be >= 0"));
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Product not found")))
                .filter(p -> p.getBranchId().equals(branchId))
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Product not found in branch")))
                .flatMap(p -> productRepository.updateStock(productId, newStock))
                .doOnNext(p -> log.info("Updated stock product {} -> {}", productId, p.getStock()))
                .doOnError(e -> log.error("Error updating stock", e));
    }
}
