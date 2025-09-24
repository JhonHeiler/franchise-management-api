package com.example.franchise.infrastructure.adapter.rest.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AggregationResultDto {
    String branchId;
    String branchName;
    String productId;
    String productName;
    int stock;
}
