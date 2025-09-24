package com.example.franchise.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Product {
    String id;
    String branchId;
    String name;
    int stock;
}
