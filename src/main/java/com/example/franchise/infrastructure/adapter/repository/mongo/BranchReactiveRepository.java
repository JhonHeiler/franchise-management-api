package com.example.franchise.infrastructure.adapter.repository.mongo;

import com.example.franchise.infrastructure.adapter.repository.mongo.document.BranchDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchReactiveRepository extends ReactiveMongoRepository<BranchDocument, String> {
    Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name);
    Flux<BranchDocument> findByFranchiseId(String franchiseId);
}
