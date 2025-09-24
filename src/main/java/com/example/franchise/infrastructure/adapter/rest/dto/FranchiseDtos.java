package com.example.franchise.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

public interface FranchiseDtos {
    @Value @Builder class CreateFranchiseRequest { @NotBlank String name; }
    @Value @Builder class FranchiseResponse { String id; String name; }
    @Value @Builder class RenameFranchiseRequest { @NotBlank String name; }
}
