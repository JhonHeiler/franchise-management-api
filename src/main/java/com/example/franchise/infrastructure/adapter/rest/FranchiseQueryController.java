package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.GetFranchiseWithBranchesUseCase;
import com.example.franchise.application.usecase.GetFranchiseWithBranchesUseCase.FranchiseAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
public class FranchiseQueryController {
    private final GetFranchiseWithBranchesUseCase getFranchiseWithBranchesUseCase;

    @GetMapping("/{franchiseId}/with-branches")
    public Mono<FranchiseAggregate> getWithBranches(@PathVariable String franchiseId) {
        return getFranchiseWithBranchesUseCase.execute(franchiseId);
    }
}