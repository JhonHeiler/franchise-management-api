package com.example.franchise.infrastructure.adapter.repository.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("franchises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseDocument {
    @Id
    private String id;
    private String name;
}
