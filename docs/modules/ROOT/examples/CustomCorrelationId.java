package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.HttpRequest;

@ApplicationScoped
public class CustomCorrelationId implements CorrelationId {
    @Override
    public String generate(HttpRequest request) {
        return "custom-id";
    }
}
