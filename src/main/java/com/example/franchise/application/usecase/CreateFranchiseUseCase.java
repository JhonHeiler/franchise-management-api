package com.example.franchise.application.usecase;

import com.example.franchise.domain.model.Franchise;
import com.example.franchise.domain.port.FranchiseRepository;
import com.example.franchise.application.usecase.errors.DomainErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CreateFranchiseUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateFranchiseUseCase.class);
    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(String name) {
        if(name == null || name.isBlank()) {
            return Mono.error(new DomainErrors.Validation("Franchise name must not be blank"));
        }
        return franchiseRepository.existsByName(name)
                .flatMap(exists -> exists ? Mono.error(new DomainErrors.Conflict("Franchise name already exists")) : Mono.just(true))
                .map(ignored -> Franchise.builder().id(UUID.randomUUID().toString()).name(name).build())
                .flatMap(franchiseRepository::save)
                .doOnNext(f -> log.info("Created franchise {}", f.getId()))
                .doOnError(e -> log.error("Error creating franchise", e));
    }
}
