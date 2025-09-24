package com.example.franchise.infrastructure.adapter.repository.mongo;

import com.example.franchise.domain.model.Product;
import com.example.franchise.domain.port.ProductRepository;
import com.example.franchise.infrastructure.adapter.mapper.DocumentMapper;
import com.example.franchise.infrastructure.adapter.repository.mongo.document.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
    @Profile("!inmem")
public class ProductRepositoryAdapter implements ProductRepository {
    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryAdapter.class);
    private final ProductReactiveRepository repository;
    private final ReactiveMongoTemplate template;

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(DocumentMapper.toDocument(product)).map(DocumentMapper::toDomain)
                .doOnNext(p -> log.debug("Saved product {}", p.getId()));
    }

    @Override
    public Mono<Product> findById(String id) { return repository.findById(id).map(DocumentMapper::toDomain); }

    @Override
    public Mono<Boolean> existsByBranchIdAndName(String branchId, String name) { return repository.existsByBranchIdAndName(branchId, name); }

    @Override
    public Flux<Product> findByBranchId(String branchId) { return repository.findByBranchId(branchId).map(DocumentMapper::toDomain); }

    @Override
    public Mono<Void> deleteById(String productId) { return repository.deleteById(productId); }

    @Override
    public Mono<Product> updateStock(String productId, int newStock) {
        return template.findAndModify(Query.query(Criteria.where("_id").is(productId)), new Update().set("stock", newStock), ProductDocument.class)
                .map(DocumentMapper::toDomain);
    }

    @Override
    public Mono<Product> rename(String productId, String newName) {
        return template.findAndModify(Query.query(Criteria.where("_id").is(productId)), new Update().set("name", newName), ProductDocument.class)
                .map(DocumentMapper::toDomain);
    }
}
