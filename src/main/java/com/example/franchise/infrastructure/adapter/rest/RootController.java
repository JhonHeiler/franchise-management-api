package com.example.franchise.infrastructure.adapter.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/** Simple landing endpoint so hitting http://localhost:8080/ shows useful info instead of a 404. */
@RestController
public class RootController {

    private final String appName;
    private final String appVersion;
    private final Environment env;

    public RootController(@Value("${spring.application.name:franchise-api}") String appName,
                          @Value("${app.version:0.0.1-SNAPSHOT}") String appVersion,
                          Environment env) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.env = env;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> index() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", appName);
        body.put("version", appVersion);
        body.put("activeProfiles", Arrays.asList(env.getActiveProfiles()));
        body.put("links", Map.of(
                "ping", "/ping",
                "swaggerUi", "/swagger-ui.html",
                "openApi", "/v3/api-docs",
                "actuator", "/actuator"
        ));
        body.put("description", "Reactive Franchise Management API - use the provided links to explore.");
        return body;
    }
}
