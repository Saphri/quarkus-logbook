package io.quarkiverse.logbook.runtime.spi;

import org.jboss.logging.Logger;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;

/**
 * An implementation of {@link HttpLogWriter} that uses JBoss Logging to write log messages.
 * This class integrates Logbook with Quarkus's logging framework.
 */
public final class QuarkusHttpLogWriter implements HttpLogWriter {

    private final Logger log = Logger.getLogger(Logbook.class);

    /**
     * Checks if the writer is active.
     * The writer is considered active if the TRACE log level is enabled for the Logbook category.
     *
     * @return {@code true} if TRACE level is enabled, {@code false} otherwise.
     */
    @Override
    public boolean isActive() {
        return log.isTraceEnabled();
    }

    /**
     * Writes a request log message at the TRACE level.
     *
     * @param precorrelation the precorrelation containing the request details.
     * @param request the formatted request string.
     */
    @Override
    public void write(final Precorrelation precorrelation, final String request) {
        log.trace(request);
    }

    /**
     * Writes a response log message at the TRACE level.
     *
     * @param correlation the correlation containing the request and response details.
     * @param response the formatted response string.
     */
    @Override
    public void write(final Correlation correlation, final String response) {
        log.trace(response);
    }
}
