package com.example.franchise.infrastructure.adapter.repository.mongo;

import com.example.franchise.infrastructure.adapter.repository.mongo.document.ProductDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReactiveRepository extends ReactiveMongoRepository<ProductDocument, String> {
    Mono<Boolean> existsByBranchIdAndName(String branchId, String name);
    Flux<ProductDocument> findByBranchId(String branchId);
}
