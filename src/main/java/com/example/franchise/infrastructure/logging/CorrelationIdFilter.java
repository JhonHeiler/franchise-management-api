package com.example.franchise.infrastructure.logging;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Adds / propagates a Correlation ID for each request:
 *  - Reads header "X-Correlation-Id" if present
 *  - Generates a UUID otherwise
 *  - Exposes it back in response header and MDC (logging)
 *  - Puts it into Reactor context under key "correlationId"
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements WebFilter {
    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String incoming = exchange.getRequest().getHeaders().getFirst(HEADER);
        String correlationId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;
        exchange.getResponse().getHeaders().set(HEADER, correlationId);

        return chain.filter(exchange)
                // Store in Reactor context for downstream code if needed
                .contextWrite(ctx -> ctx.put(MDC_KEY, correlationId))
                .doOnSubscribe(sub -> MDC.put(MDC_KEY, correlationId))
                .doFinally(sig -> MDC.remove(MDC_KEY));
    }
}
