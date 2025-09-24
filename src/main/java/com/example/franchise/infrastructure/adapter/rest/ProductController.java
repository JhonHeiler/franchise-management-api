package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.*;
import com.example.franchise.infrastructure.adapter.rest.dto.ProductDtos.*;
import com.example.franchise.application.usecase.GetMaxStockProductPerBranchUseCase.BranchMaxStock;
import com.example.franchise.infrastructure.adapter.rest.dto.AggregationResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franchises/{franchiseId}/branches/{branchId}/products")
@RequiredArgsConstructor
public class ProductController {
    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final RemoveProductFromBranchUseCase removeProductFromBranchUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final RenameProductUseCase renameProductUseCase;
    @Autowired(required = false)
    private GetMaxStockProductPerBranchUseCase getMaxStockProductPerBranchUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> create(@PathVariable String franchiseId, @PathVariable String branchId, @Validated @RequestBody CreateProductRequest request) {
        return addProductToBranchUseCase.execute(franchiseId, branchId, request.getName(), request.getStock())
                .map(p -> ProductResponse.builder().id(p.getId()).branchId(p.getBranchId()).name(p.getName()).stock(p.getStock()).build());
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String franchiseId, @PathVariable String branchId, @PathVariable String productId) {
        return removeProductFromBranchUseCase.execute(franchiseId, branchId, productId);
    }

    @PatchMapping("/{productId}/stock")
    public Mono<ProductResponse> updateStock(@PathVariable String franchiseId, @PathVariable String branchId, @PathVariable String productId, @Validated @RequestBody UpdateStockRequest request) {
        return updateProductStockUseCase.execute(franchiseId, branchId, productId, request.getStock())
                .map(p -> ProductResponse.builder().id(p.getId()).branchId(p.getBranchId()).name(p.getName()).stock(p.getStock()).build());
    }

    @PatchMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> rename(@PathVariable String franchiseId, @PathVariable String branchId, @PathVariable String productId, @Validated @RequestBody RenameProductRequest request) {
        return renameProductUseCase.execute(franchiseId, branchId, productId, request.getName());
    }

    private AggregationResultDto toAggregationDto(BranchMaxStock r) {
        return AggregationResultDto.builder()
                .branchId(r.branchId())
                .branchName(r.branchName())
                .productId(r.productId())
                .productName(r.productName())
                .stock(r.stock())
                .build();
    }
}
