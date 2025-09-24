package com.example.franchise.application.usecase;

import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetFranchiseWithBranchesUseCase {
    private static final Logger log = LoggerFactory.getLogger(GetFranchiseWithBranchesUseCase.class);
    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public record FranchiseAggregate(Franchise franchise, java.util.List<Branch> branches) {}

    /** Example use-case demonstrating zip operator combining two async sources */
    public Mono<FranchiseAggregate> execute(String franchiseId) {
        Mono<Franchise> f = franchiseRepository.findById(franchiseId);
        Mono<java.util.List<Branch>> bs = branchRepository.findByFranchiseId(franchiseId).collectList();
        return Mono.zip(f, bs)
                .map(tuple -> new FranchiseAggregate(tuple.getT1(), tuple.getT2()))
                .doOnNext(agg -> log.debug("Loaded franchise {} with {} branches", franchiseId, agg.branches().size()));
    }
}