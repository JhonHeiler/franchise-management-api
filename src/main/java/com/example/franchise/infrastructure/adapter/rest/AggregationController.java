package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.GetMaxStockProductPerBranchUseCase;
import com.example.franchise.application.usecase.GetMaxStockProductPerBranchUseCase.BranchMaxStock;
import com.example.franchise.infrastructure.adapter.rest.dto.AggregationResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Profile("!inmem")
@RequestMapping("/api/v1/franchises/{franchiseId}/branches/max-stock-products")
@RequiredArgsConstructor
public class AggregationController {
    private final GetMaxStockProductPerBranchUseCase getMaxStockProductPerBranchUseCase;

    @GetMapping
    public Flux<AggregationResultDto> max(@PathVariable String franchiseId) {
        return getMaxStockProductPerBranchUseCase.execute(franchiseId)
                .map(this::map);
    }

    private AggregationResultDto map(BranchMaxStock r) { return AggregationResultDto.builder().branchId(r.branchId()).branchName(r.branchName()).productId(r.productId()).productName(r.productName()).stock(r.stock()).build(); }
}
