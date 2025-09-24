package com.example.franchise.infrastructure.logging;

import org.slf4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class ReactorLogging {
    private ReactorLogging() {}

    public static <T> Mono<T> logMono(Mono<T> mono, Logger log, String operation) {
        return mono.doOnSubscribe(s -> log.debug("start {}", operation))
                .doOnSuccess(v -> log.debug("success {}", operation))
                .doOnError(e -> log.error("error {}: {}", operation, e.getMessage()));
    }

    public static <T> Flux<T> logFlux(Flux<T> flux, Logger log, String operation) {
        return flux.doOnSubscribe(s -> log.debug("start {}", operation))
                .doOnComplete(() -> log.debug("complete {}", operation))
                .doOnError(e -> log.error("error {}: {}", operation, e.getMessage()));
    }
}
