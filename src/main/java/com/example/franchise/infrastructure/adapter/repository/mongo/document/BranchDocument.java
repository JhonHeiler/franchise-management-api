package com.example.franchise.infrastructure.adapter.repository.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("branches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDocument {
    @Id
    private String id;
    private String franchiseId;
    private String name;
}
