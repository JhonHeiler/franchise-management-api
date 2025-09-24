package com.example.franchise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Single Spring Boot entrypoint. Use case beans are discovered via @Component on each use case.

@SpringBootApplication
public class FranchiseApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FranchiseApiApplication.class, args);
    }
}
