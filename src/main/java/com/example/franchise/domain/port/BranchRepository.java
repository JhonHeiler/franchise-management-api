package com.example.franchise.domain.port;

import com.example.franchise.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(String id);
    Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name);
    Flux<Branch> findByFranchiseId(String franchiseId);
    Mono<Branch> rename(String branchId, String newName);
}
