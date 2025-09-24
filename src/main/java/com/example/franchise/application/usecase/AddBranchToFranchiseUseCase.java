package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.domain.port.BranchRepository;
import com.example.franchise.domain.port.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AddBranchToFranchiseUseCase {
    private static final Logger log = LoggerFactory.getLogger(AddBranchToFranchiseUseCase.class);
    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public Mono<Branch> execute(String franchiseId, String branchName) {
        if (branchName == null || branchName.isBlank()) return Mono.error(new DomainErrors.Validation("Branch name must not be blank"));
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Franchise not found")))
                .flatMap(f -> branchRepository.existsByFranchiseIdAndName(franchiseId, branchName)
                        .flatMap(exists -> exists ? Mono.error(new DomainErrors.Conflict("Branch name already exists in franchise")) : Mono.just(true)))
                .map(ignored -> Branch.builder().id(UUID.randomUUID().toString()).franchiseId(franchiseId).name(branchName).build())
                .flatMap(branchRepository::save)
                .doOnNext(b -> log.info("Added branch {} to franchise {}", b.getId(), franchiseId))
                .doOnError(e -> log.error("Error adding branch", e));
    }
}
