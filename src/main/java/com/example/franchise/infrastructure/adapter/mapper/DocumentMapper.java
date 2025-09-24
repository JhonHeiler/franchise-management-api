package com.example.franchise.infrastructure.adapter.mapper;

import com.example.franchise.domain.model.*;
import com.example.franchise.infrastructure.adapter.repository.mongo.document.*;

public final class DocumentMapper {
    private DocumentMapper() {}

    public static Franchise toDomain(FranchiseDocument d) {
        return Franchise.builder().id(d.getId()).name(d.getName()).build();
    }
    public static FranchiseDocument toDocument(Franchise f) {
        return FranchiseDocument.builder().id(f.getId()).name(f.getName()).build();
    }

    public static Branch toDomain(BranchDocument d) {
        return Branch.builder().id(d.getId()).franchiseId(d.getFranchiseId()).name(d.getName()).build();
    }
    public static BranchDocument toDocument(Branch b) {
        return BranchDocument.builder().id(b.getId()).franchiseId(b.getFranchiseId()).name(b.getName()).build();
    }

    public static Product toDomain(ProductDocument d) {
        return Product.builder().id(d.getId()).branchId(d.getBranchId()).name(d.getName()).stock(d.getStock()).build();
    }
    public static ProductDocument toDocument(Product p) {
        return ProductDocument.builder().id(p.getId()).branchId(p.getBranchId()).name(p.getName()).stock(p.getStock()).build();
    }
}
