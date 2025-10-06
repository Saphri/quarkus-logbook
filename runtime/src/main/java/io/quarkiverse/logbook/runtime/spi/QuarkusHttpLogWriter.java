package io.quarkiverse.logbook.runtime.spi;

import org.jboss.logging.Logger;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;

public final class QuarkusHttpLogWriter implements HttpLogWriter {

    private final Logger log = Logger.getLogger(Logbook.class);

    @Override
    public boolean isActive() {
        return log.isTraceEnabled();
    }

    @Override
    public void write(final Precorrelation precorrelation, final String request) {
        log.trace(request);
    }

    @Override
    public void write(final Correlation correlation, final String response) {
        log.trace(response);
    }

}
