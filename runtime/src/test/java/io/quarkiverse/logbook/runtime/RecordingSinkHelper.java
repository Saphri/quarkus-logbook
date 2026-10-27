package io.quarkiverse.logbook.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;

/**
 * Test helper: a recording {@link HttpLogFormatter} and {@link HttpLogWriter} pair.
 * A single instance can serve as both roles of a {@link DefaultSink}: the formatter renders
 * a message from the (already filtered) request or response body, and the writer appends it
 * to a shared list of messages that tests assert on.
 */
public class RecordingSinkHelper implements HttpLogFormatter, HttpLogWriter {

    private final ArrayList<String> messages;

    /**
     * Constructs a new helper that records rendered messages into the provided list.
     *
     * @param messages the list that records all written messages.
     */
    public RecordingSinkHelper(final ArrayList<String> messages) {
        this.messages = messages;
    }

    /**
     * Formats a request by rendering its loggable body.
     *
     * @param precorrelation the precorrelation of the exchange.
     * @param request the request to format.
     * @return the body rendered as a string.
     */
    @Override
    public String format(final Precorrelation precorrelation, final HttpRequest request) throws java.io.IOException {
        return java.util.Arrays.toString(request.getBody());
    }

    /**
     * Formats a response by rendering its loggable body.
     *
     * @param correlation the correlation of the exchange.
     * @param response the response to format.
     * @return the body rendered as a string.
     */
    @Override
    public String format(final Correlation correlation, final HttpResponse response) throws java.io.IOException {
        return java.util.Arrays.toString(response.getBody());
    }

    /**
     * Records a formatted request message.
     *
     * @param precorrelation the precorrelation of the exchange.
     * @param message the formatted message.
     */
    @Override
    public void write(final Precorrelation precorrelation, final String message) throws java.io.IOException {
        messages.add(message);
    }

    /**
     * Records a formatted response message.
     *
     * @param correlation the correlation of the exchange.
     * @param message the formatted message.
     */
    @Override
    public void write(final Correlation correlation, final String message) throws java.io.IOException {
        messages.add(message);
    }
}
