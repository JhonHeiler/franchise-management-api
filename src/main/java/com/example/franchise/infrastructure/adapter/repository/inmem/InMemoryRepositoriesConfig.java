package com.example.franchise.infrastructure.adapter.repository.inmem;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.FranchiseRepository;
import com.example.franchise.domain.port.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Simple in-memory repositories for demo / liveness when MongoDB is not available. */
@Configuration
@Profile("inmem")
public class InMemoryRepositoriesConfig {

    @Bean
    public FranchiseRepository franchiseRepository() {
        return new FranchiseRepository() {
            private final Map<String, Franchise> store = new ConcurrentHashMap<>();
            @Override public Mono<Franchise> save(Franchise franchise) {
                String id = franchise.getId() != null ? franchise.getId() : UUID.randomUUID().toString();
                Franchise saved = franchise.withId(id);
                store.put(id, saved);
                return Mono.just(saved);
            }
            @Override public Mono<Franchise> findById(String id) { return Mono.justOrEmpty(store.get(id)); }
            @Override public Mono<Boolean> existsByName(String name) { return Mono.just(store.values().stream().anyMatch(f -> f.getName().equalsIgnoreCase(name))); }
            @Override public Mono<Franchise> rename(String id, String newName) { return Mono.justOrEmpty(store.computeIfPresent(id, (k,v)-> v.withName(newName))); }
            @Override public Flux<Franchise> findAll() { return Flux.fromIterable(store.values()); }
        };
    }

    @Bean
    public BranchRepository branchRepository() {
        return new BranchRepository() {
            private final Map<String, Branch> store = new ConcurrentHashMap<>();
            @Override public Mono<Branch> save(Branch branch) {
                String id = branch.getId() != null ? branch.getId() : UUID.randomUUID().toString();
                Branch saved = branch.withId(id);
                store.put(id, saved);
                return Mono.just(saved);
            }
            @Override public Mono<Branch> findById(String id) { return Mono.justOrEmpty(store.get(id)); }
            @Override public Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name) { return Mono.just(store.values().stream().anyMatch(b -> b.getFranchiseId().equals(franchiseId) && b.getName().equalsIgnoreCase(name))); }
            @Override public Flux<Branch> findByFranchiseId(String franchiseId) { return Flux.fromStream(store.values().stream().filter(b -> b.getFranchiseId().equals(franchiseId))); }
            @Override public Mono<Branch> rename(String branchId, String newName) { return Mono.justOrEmpty(store.computeIfPresent(branchId, (k,v)-> v.withName(newName))); }
        };
    }

    @Bean
    public ProductRepository productRepository() {
        return new ProductRepository() {
            private final Map<String, Product> store = new ConcurrentHashMap<>();
            @Override public Mono<Product> save(Product product) {
                String id = product.getId() != null ? product.getId() : UUID.randomUUID().toString();
                Product saved = product.withId(id);
                store.put(id, saved);
                return Mono.just(saved);
            }
            @Override public Mono<Product> findById(String id) { return Mono.justOrEmpty(store.get(id)); }
            @Override public Mono<Boolean> existsByBranchIdAndName(String branchId, String name) { return Mono.just(store.values().stream().anyMatch(p -> p.getBranchId().equals(branchId) && p.getName().equalsIgnoreCase(name))); }
            @Override public Flux<Product> findByBranchId(String branchId) { return Flux.fromStream(store.values().stream().filter(p -> p.getBranchId().equals(branchId))); }
            @Override public Mono<Void> deleteById(String productId) { store.remove(productId); return Mono.empty(); }
            @Override public Mono<Product> updateStock(String productId, int newStock) { return Mono.justOrEmpty(store.computeIfPresent(productId, (k,v)-> v.withStock(newStock))); }
            @Override public Mono<Product> rename(String productId, String newName) { return Mono.justOrEmpty(store.computeIfPresent(productId, (k,v)-> v.withName(newName))); }
        };
    }
}
