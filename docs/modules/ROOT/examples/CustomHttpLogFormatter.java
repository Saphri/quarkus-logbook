package io.quarkiverse.logbook.examples;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;

@ApplicationScoped
public class CustomHttpLogFormatter implements HttpLogFormatter {

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        return "request";
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
        return "response";
    }
}
