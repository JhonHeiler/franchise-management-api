package com.example.franchise.infrastructure.adapter.repository.mongo;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.infrastructure.adapter.mapper.DocumentMapper;
import com.example.franchise.infrastructure.adapter.repository.mongo.document.BranchDocument;
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
public class BranchRepositoryAdapter implements BranchRepository {
    private static final Logger log = LoggerFactory.getLogger(BranchRepositoryAdapter.class);
    private final BranchReactiveRepository repository;
    private final ReactiveMongoTemplate template;

    @Override
    public Mono<Branch> save(Branch branch) {
        return repository.save(DocumentMapper.toDocument(branch)).map(DocumentMapper::toDomain)
                .doOnNext(b -> log.debug("Saved branch {}", b.getId()));
    }

    @Override
    public Mono<Branch> findById(String id) { return repository.findById(id).map(DocumentMapper::toDomain); }

    @Override
    public Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name) { return repository.existsByFranchiseIdAndName(franchiseId, name); }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) { return repository.findByFranchiseId(franchiseId).map(DocumentMapper::toDomain); }

    @Override
    public Mono<Branch> rename(String branchId, String newName) {
        return template.findAndModify(Query.query(Criteria.where("_id").is(branchId)), new Update().set("name", newName), BranchDocument.class)
                .map(DocumentMapper::toDomain);
    }
}
