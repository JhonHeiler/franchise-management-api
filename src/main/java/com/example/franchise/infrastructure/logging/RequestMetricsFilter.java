package com.example.franchise.infrastructure.logging;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/** Records simple timing + logs after each request. */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class RequestMetricsFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestMetricsFilter.class);
    private final MeterRegistry registry;

    public RequestMetricsFilter(MeterRegistry registry) { this.registry = registry; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long start = System.nanoTime();
        return chain.filter(exchange)
                .doFinally(sig -> {
                    long nanos = System.nanoTime() - start;
                    long ms = TimeUnit.NANOSECONDS.toMillis(nanos);
                    ServerHttpRequest req = exchange.getRequest();
                    int status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : 200;
                    String path = req.getURI().getPath();
            String method = req.getMethod() != null ? req.getMethod().name() : "UNKNOWN";
            registry.timer("http.server.reactive.request",
                Tags.of("method", method,
                                    "status", String.valueOf(status),
                                    "path", path))
                            .record(nanos, TimeUnit.NANOSECONDS);
                    if (log.isDebugEnabled()) {
                        String corr = exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.HEADER);
                        log.debug("REQ [{}] {} {} status={} timeMs={} corr={}", method, path, sig, status, ms, corr);
                    }
                });
    }
}
