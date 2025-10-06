package io.quarkiverse.logbook.runtime.spi;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.client.api.ClientLogger;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Logbook.ResponseProcessingStage;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuarkusClientLogger implements ClientLogger {

    private final Logger log = Logger.getLogger(Logbook.class);

    private final Logbook logbook;

    private final ThreadLocal<ResponseProcessingStage> stage = new ThreadLocal<>();

    public QuarkusClientLogger(final Logbook logbook) {
        this.logbook = logbook;
    }

    @Override
    public void setBodySize(int bodySize) {
        /* no-op, logging is delegated to logbook */
    }

    @Override
    public void logResponse(HttpClientResponse response, boolean redirect) {
        response.bodyHandler(new Handler<>() {
            @Override
            public void handle(Buffer body) {
                try {
                    final var httpResponse = RemoteResponse.create(response, body);
                    stage.get().process(httpResponse).write();
                } catch (Exception e) {
                    // we can't do much here, logbook already tried its best
                    log.tracef(e, "Failed to log response for %s", response.request().absoluteURI());
                }
            }
        });
    }

    @Override
    public void logRequest(HttpClientRequest request, Buffer body, boolean omitBody) {
        try {
            final var httpRequest = LocalRequest.create(request, body);
            final var processingStage = logbook.process(httpRequest).write();
            stage.set(processingStage);
        } catch (Exception e) {
            // If we can't log the request, we can't log the response either
            log.tracef(e, "Failed to log request for %s", request.absoluteURI());
        }
    }
}
