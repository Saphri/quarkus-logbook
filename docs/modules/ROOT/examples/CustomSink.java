package io.quarkiverse.logbook.examples;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

@ApplicationScoped
public class CustomSink implements Sink {

    @Override
    public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
        // write request
    }

    @Override
    public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {
        // write response
    }
}
