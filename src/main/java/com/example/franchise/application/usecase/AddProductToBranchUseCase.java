package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AddProductToBranchUseCase {
    private static final Logger log = LoggerFactory.getLogger(AddProductToBranchUseCase.class);
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public Mono<Product> execute(String franchiseId, String branchId, String productName, int stock) {
        if (productName == null || productName.isBlank()) return Mono.error(new DomainErrors.Validation("Product name must not be blank"));
        if (stock < 0) return Mono.error(new DomainErrors.Validation("Stock must be >= 0"));
        return branchRepository.findById(branchId)
                .filter(b -> b.getFranchiseId().equals(franchiseId))
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Branch not found in franchise")))
                .flatMap(b -> productRepository.existsByBranchIdAndName(branchId, productName)
                        .flatMap(exists -> exists ? Mono.error(new DomainErrors.Conflict("Product name already exists in branch")) : Mono.just(true)))
                .map(ignored -> Product.builder().id(UUID.randomUUID().toString()).branchId(branchId).name(productName).stock(stock).build())
                .flatMap(productRepository::save)
                .doOnNext(p -> log.info("Added product {} to branch {}", p.getId(), branchId))
                .doOnError(e -> log.error("Error adding product", e));
    }
}
