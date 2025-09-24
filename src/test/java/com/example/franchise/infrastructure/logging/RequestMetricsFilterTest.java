package com.example.franchise.infrastructure.logging;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMetricsFilterTest {
    @Test
    void recordsTimer() {
        SimpleMeterRegistry reg = new SimpleMeterRegistry();
        RequestMetricsFilter f = new RequestMetricsFilter(reg);
        MockServerWebExchange ex = MockServerWebExchange.from(MockServerHttpRequest.get("/abc").build());
        WebFilterChain chain = e -> Mono.empty();
        f.filter(ex, chain).block();
        assertThat(reg.get("http.server.reactive.request").timer().count()).isEqualTo(1);
    }
}
