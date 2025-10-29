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

/**
 * An implementation of {@link ClientLogger} that integrates Logbook with the Quarkus REST client.
 * This class is responsible for intercepting outgoing client requests and incoming responses,
 * processing them with Logbook, and writing the log messages.
 */
@ApplicationScoped
public class QuarkusClientLogger implements ClientLogger {

    private static final String LOGBOOK_STAGE = "responseProcessingStage";

    private final Logger log = Logger.getLogger(Logbook.class);

    private final Logbook logbook;

    /**
     * Constructs a new QuarkusClientLogger with the given Logbook instance.
     *
     * @param logbook the Logbook instance to use for logging.
     */
    public QuarkusClientLogger(final Logbook logbook) {
        this.logbook = logbook;
    }

    /**
     * This method is a no-op as body size handling is delegated to Logbook's truncation mechanism.
     *
     * @param bodySize the maximum body size to log.
     */
    @Override
    public void setBodySize(int bodySize) {
        /* no-op, logging is delegated to logbook */
    }

    /**
     * Logs an incoming client response.
     * This method reads the response body and then processes the response with Logbook.
     *
     * @param response the Vert.x HTTP client response.
     * @param redirect a flag indicating if the response is a redirect.
     */
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
                    log.warnf(e, "Failed to log response for %s", response.request().absoluteURI());
                }
            }
        });
    }

    /**
     * Logs an outgoing client request.
     * This method processes the request with Logbook and stores the response processing stage
     * in the Vert.x context for later use by {@link #logResponse(HttpClientResponse, boolean)}.
     *
     * @param request the Vert.x HTTP client request.
     * @param body the request body buffer.
     * @param omitBody a flag indicating if the body should be omitted from logging.
     */
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
            log.warnf(e, "Failed to log request for %s", request.absoluteURI());
        }
    }
}
