package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.port.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RenameBranchUseCase {
    private static final Logger log = LoggerFactory.getLogger(RenameBranchUseCase.class);
    private final BranchRepository branchRepository;

    public Mono<Void> execute(String franchiseId, String branchId, String newName) {
        if (newName == null || newName.isBlank()) return Mono.error(new DomainErrors.Validation("Name must not be blank"));
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Branch not found")))
                .filter(b -> b.getFranchiseId().equals(franchiseId))
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Branch not found in franchise")))
                .flatMap(b -> branchRepository.rename(branchId, newName))
                .doOnNext(b -> log.info("Renamed branch {}", branchId))
                .then();
    }
}
