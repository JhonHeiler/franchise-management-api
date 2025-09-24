package com.example.franchise.domain.port;

import com.example.franchise.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Mono<Boolean> existsByBranchIdAndName(String branchId, String name);
    Flux<Product> findByBranchId(String branchId);
    Mono<Void> deleteById(String productId);
    Mono<Product> updateStock(String productId, int newStock);
    Mono<Product> rename(String productId, String newName);
}
