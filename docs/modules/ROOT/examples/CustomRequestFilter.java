package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.RequestFilter;

@ApplicationScoped
public class CustomRequestFilter implements RequestFilter {
    @Override
    public HttpRequest filter(HttpRequest request) {
        return request;
    }
}
