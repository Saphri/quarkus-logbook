package io.quarkiverse.logbook.runtime.spi;

import java.io.IOException;

import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Origin;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;

public class RemoteResponse implements HttpResponse {
    private final int status;
    private final HttpHeaders headers;
    private final byte[] body;

    private boolean withBody = false;

    public RemoteResponse(final int status, final HttpHeaders headers, final byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create(final HttpClientResponse response, final Buffer body) {
        return new RemoteResponse(
            response.statusCode(),
            HeaderUtils.toLogbookHeaders(response.headers()),
            body == null ? new byte[0] : body.getBytes()
        );
    }

    @Override
    public Origin getOrigin() {
        return Origin.REMOTE;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public byte[] getBody() throws IOException {
        return withBody && body != null ? body : new byte[0];
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public HttpResponse withBody() throws IOException {
        withBody = true;
        return this;
    }

    @Override
    public HttpResponse withoutBody() {
        withBody = false;
        return this;
    }
}
