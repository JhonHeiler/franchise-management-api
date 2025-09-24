package com.example.franchise.infrastructure.logging;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void generatesCorrelationIdWhenMissing() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        WebFilterChain chain = ex -> Mono.empty();

        filter.filter(exchange, chain).block();

        String header = exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.HEADER);
        assertThat(header).isNotBlank();
    }

    @Test
    void reusesIncomingCorrelationId() {
        String provided = "abc-123";
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(CorrelationIdFilter.HEADER, provided).build());
        WebFilterChain chain = ex -> Mono.empty();

        filter.filter(exchange, chain).block();

        String header = exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.HEADER);
        assertThat(header).isEqualTo(provided);
    }
}
