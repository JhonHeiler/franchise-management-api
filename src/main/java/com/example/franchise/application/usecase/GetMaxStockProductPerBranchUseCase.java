package com.example.franchise.application.usecase;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.example.franchise.application.usecase.errors.DomainErrors;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

@Component
@Profile("!inmem")
@RequiredArgsConstructor
public class GetMaxStockProductPerBranchUseCase {
    private static final Logger log = LoggerFactory.getLogger(GetMaxStockProductPerBranchUseCase.class);
    private final ReactiveMongoTemplate template;

    public record BranchMaxStock(String branchId, String branchName, String productId, String productName, int stock) {}

    public Flux<BranchMaxStock> execute(String franchiseId) {
    AggregationOperation matchBranches = Aggregation.match(Criteria.where("franchiseId").is(franchiseId));
    AggregationOperation lookupProducts = Aggregation.lookup("products", "_id", "branchId", "products");
    AggregationOperation unwind = Aggregation.unwind("$products");
    AggregationOperation sort = Aggregation.sort(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "products.stock"));
    AggregationOperation group = Aggregation.group("_id", "name")
        .first("_id").as("branchId")
        .first("name").as("branchName")
        .first("products._id").as("productId")
        .first("products.name").as("productName")
        .first("products.stock").as("stock");
    AggregationOperation project = Aggregation.project("branchId", "branchName", "productId", "productName", "stock");

    Aggregation aggregation = Aggregation.newAggregation(matchBranches, lookupProducts, unwind, sort, group, project);
    return template.aggregate(aggregation, "branches", BranchMaxStock.class)
        .switchIfEmpty(Flux.error(new DomainErrors.NotFound("No branches/products for franchise")))
        .doOnNext(r -> log.debug("Aggregation result: {}", r))
        .doOnError(e -> log.error("Aggregation error", e))
        .doOnComplete(() -> log.debug("Aggregation completed for franchise {}", franchiseId));
    }
}
