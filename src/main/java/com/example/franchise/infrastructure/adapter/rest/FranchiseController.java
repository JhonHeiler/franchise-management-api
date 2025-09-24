package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.*;
import com.example.franchise.domain.model.Franchise;
import com.example.franchise.infrastructure.adapter.rest.dto.FranchiseDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
public class FranchiseController {
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final RenameFranchiseUseCase renameFranchiseUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponse> create(@Validated @RequestBody CreateFranchiseRequest request) {
        return createFranchiseUseCase.execute(request.getName())
                .map(this::toResponse);
    }

    @PatchMapping("/{franchiseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> rename(@PathVariable String franchiseId, @Validated @RequestBody RenameFranchiseRequest request) {
        return renameFranchiseUseCase.execute(franchiseId, request.getName());
    }

    private FranchiseResponse toResponse(Franchise f) { return FranchiseResponse.builder().id(f.getId()).name(f.getName()).build(); }
}
