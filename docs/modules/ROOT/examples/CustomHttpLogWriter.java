package io.quarkiverse.logbook.examples;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

@ApplicationScoped
public class CustomHttpLogWriter implements HttpLogWriter {

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void write(Precorrelation precorrelation, String request) throws IOException {
        // write request
    }

    @Override
    public void write(Correlation correlation, String response) throws IOException {
        // write response
    }
}
