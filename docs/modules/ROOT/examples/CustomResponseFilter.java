package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.ResponseFilter;

@ApplicationScoped
public class CustomResponseFilter implements ResponseFilter {
    @Override
    public HttpResponse filter(HttpResponse response) {
        return response;
    }
}
