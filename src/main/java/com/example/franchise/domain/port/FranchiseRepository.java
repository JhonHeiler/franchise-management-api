package com.example.franchise.domain.port;

import com.example.franchise.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Mono<Boolean> existsByName(String name);
    Mono<Franchise> rename(String id, String newName);
    Flux<Franchise> findAll();
}
