package com.example.franchise.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Branch {
    String id;
    String franchiseId;
    String name;
}
