package com.example.franchise.application.usecase;

import com.example.franchise.application.usecase.errors.DomainErrors;
import com.example.franchise.domain.port.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RenameFranchiseUseCase {
    private static final Logger log = LoggerFactory.getLogger(RenameFranchiseUseCase.class);
    private final FranchiseRepository franchiseRepository;

    public Mono<Void> execute(String franchiseId, String newName) {
        if (newName == null || newName.isBlank()) return Mono.error(new DomainErrors.Validation("Name must not be blank"));
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new DomainErrors.NotFound("Franchise not found")))
                .flatMap(f -> franchiseRepository.existsByName(newName)
                        .flatMap(exists -> exists ? Mono.error(new DomainErrors.Conflict("Name already exists")) : Mono.just(true)))
                .flatMap(x -> franchiseRepository.rename(franchiseId, newName))
                .doOnNext(f -> log.info("Renamed franchise {}", franchiseId))
                .then();
    }
}
