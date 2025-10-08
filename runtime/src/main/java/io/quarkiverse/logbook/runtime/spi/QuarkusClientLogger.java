package io.quarkiverse.logbook.runtime.spi;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.client.api.ClientLogger;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Logbook.ResponseProcessingStage;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

@ApplicationScoped
public class QuarkusClientLogger implements ClientLogger {

    private static final String LOGBOOK_STAGE = "responseProcessingStage";

    private final Logger log = Logger.getLogger(Logbook.class);

    private final Logbook logbook;

    public QuarkusClientLogger(final Logbook logbook) {
        this.logbook = logbook;
    }

    @Override
    public void setBodySize(int bodySize) {
        /* no-op, logging is delegated to logbook */
    }

    @Override
    public void logResponse(HttpClientResponse response, boolean redirect) {
        final var context = Vertx.currentContext();
        if (context == null) {
            // should never happen in practice
            log.warn("No Vert.x context found, cannot log response");
            return;
        }
        response.bodyHandler(new Handler<>() {
            @Override
            public void handle(Buffer body) {
                try {
                    final var httpResponse = RemoteResponse.create(response, body);
                    final var stage = (ResponseProcessingStage) context.getLocal(LOGBOOK_STAGE);
                    stage.process(httpResponse).write();
                } catch (Exception e) {
                    // we can't do much here, logbook already tried its best
                    log.tracef(e, "Failed to log response for %s", response.request().absoluteURI());
                }
            }
        });
    }

    @Override
    public void logRequest(HttpClientRequest request, Buffer body, boolean omitBody) {
        final var context = Vertx.currentContext();
        if (context == null) {
            // should never happen in practice
            log.warn("No Vert.x context found, cannot log request");
            return;
        }
        try {
            final var httpRequest = LocalRequest.create(request, body);
            final var processingStage = logbook.process(httpRequest).write();
            context.putLocal(LOGBOOK_STAGE, processingStage);
        } catch (Exception e) {
            // If we can't log the request, we can't log the response either
            log.tracef(e, "Failed to log request for %s", request.absoluteURI());
        }
    }
}
