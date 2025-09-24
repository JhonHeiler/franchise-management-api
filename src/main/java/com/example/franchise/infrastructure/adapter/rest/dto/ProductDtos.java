package com.example.franchise.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

public interface ProductDtos {
    @Value @Builder class CreateProductRequest { @NotBlank String name; @Min(0) int stock; }
    @Value @Builder class ProductResponse { String id; String branchId; String name; int stock; }
    @Value @Builder class UpdateStockRequest { @Min(0) int stock; }
    @Value @Builder class RenameProductRequest { @NotBlank String name; }
}
