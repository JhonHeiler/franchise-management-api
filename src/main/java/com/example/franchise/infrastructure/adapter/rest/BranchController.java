package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.AddBranchToFranchiseUseCase;
import com.example.franchise.application.usecase.RenameBranchUseCase;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.infrastructure.adapter.rest.dto.BranchDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franchises/{franchiseId}/branches")
@RequiredArgsConstructor
public class BranchController {
    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final RenameBranchUseCase renameBranchUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BranchResponse> create(@PathVariable String franchiseId, @Validated @RequestBody CreateBranchRequest request) {
        return addBranchToFranchiseUseCase.execute(franchiseId, request.getName()).map(this::toResponse);
    }

    @PatchMapping("/{branchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> rename(@PathVariable String franchiseId, @PathVariable String branchId, @Validated @RequestBody RenameBranchRequest request) {
        return renameBranchUseCase.execute(franchiseId, branchId, request.getName());
    }

    private BranchResponse toResponse(Branch b) { return BranchResponse.builder().id(b.getId()).franchiseId(b.getFranchiseId()).name(b.getName()).build(); }
}
