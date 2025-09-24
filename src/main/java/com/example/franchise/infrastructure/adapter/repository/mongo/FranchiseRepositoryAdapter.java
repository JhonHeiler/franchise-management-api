package com.example.franchise.infrastructure.adapter.repository.mongo;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.port.FranchiseRepository;
import com.example.franchise.infrastructure.adapter.mapper.DocumentMapper;
import com.example.franchise.infrastructure.adapter.repository.mongo.document.FranchiseDocument;
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
public class FranchiseRepositoryAdapter implements FranchiseRepository {
    private static final Logger log = LoggerFactory.getLogger(FranchiseRepositoryAdapter.class);
    private final FranchiseReactiveRepository repository;
    private final ReactiveMongoTemplate template;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return repository.save(DocumentMapper.toDocument(franchise))
                .map(DocumentMapper::toDomain)
                .doOnNext(f -> log.debug("Saved franchise {}", f.getId()));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id).map(DocumentMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) { return repository.existsByName(name); }

    @Override
    public Mono<Franchise> rename(String id, String newName) {
        return template.findAndModify(Query.query(Criteria.where("_id").is(id)), new Update().set("name", newName), FranchiseDocument.class)
                .map(DocumentMapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() { return repository.findAll().map(DocumentMapper::toDomain); }
}
