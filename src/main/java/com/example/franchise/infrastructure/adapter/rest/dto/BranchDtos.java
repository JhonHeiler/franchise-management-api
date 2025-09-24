package com.example.franchise.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

public interface BranchDtos {
    @Value @Builder class CreateBranchRequest { @NotBlank String name; }
    @Value @Builder class BranchResponse { String id; String franchiseId; String name; }
    @Value @Builder class RenameBranchRequest { @NotBlank String name; }
}
