package com.example.franchise.infrastructure.adapter.repository.mongo;

import com.example.franchise.infrastructure.adapter.repository.mongo.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface FranchiseReactiveRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
    Mono<Boolean> existsByName(String name);
}
