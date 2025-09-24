package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RenameProductUseCase {
    private static final Logger log = LoggerFactory.getLogger(RenameProductUseCase.class);
    private final ProductRepository productRepository;

    public Mono<Void> execute(String franchiseId, String branchId, String productId, String newName) {
        if (newName == null || newName.isBlank()) return Mono.error(new DomainErrors.Validation("Name must not be blank"));
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Product not found")))
                .filter(p -> p.getBranchId().equals(branchId))
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Product not in branch")))
                .flatMap(p -> productRepository.rename(productId, newName))
                .doOnNext(p -> log.info("Renamed product {}", productId))
                .then();
    }
}
