package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.HttpHeaders;

@ApplicationScoped
public class CustomHeaderFilter implements HeaderFilter {
    @Override
    public HttpHeaders filter(HttpHeaders headers) {
        return headers;
    }
}
