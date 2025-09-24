package com.example.franchise.application.usecase.errors;

public final class DomainErrors {
    private DomainErrors() {}

    public static final class NotFound extends RuntimeException {
        public NotFound(String message) { super(message); }
    }
    public static final class Conflict extends RuntimeException {
        public Conflict(String message) { super(message); }
    }
    public static final class Validation extends RuntimeException {
        public Validation(String message) { super(message); }
    }
}
