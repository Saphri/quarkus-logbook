package io.quarkiverse.logbook.runtime.spi;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Origin;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;

public class LocalRequest implements HttpRequest {

    private final URI uri;
    private final HttpMethod httpMethod;
    private final HttpHeaders headers;
    private final byte[] body;

    private boolean withBody = false;

    public LocalRequest(final URI uri, final HttpMethod httpMethod, final HttpHeaders headers, final byte[] body) {
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest create(final HttpClientRequest request, final Buffer buffer) {
        return new LocalRequest(
                URI.create(request.absoluteURI()),
                request.getMethod(),
                HeaderUtils.toLogbookHeaders(request.headers()),
                buffer == null ? new byte[0] : buffer.getBytes());
    }

    @Override
    public Origin getOrigin() {
        return Origin.LOCAL;
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
    public String getRemote() {
        return "localhost";
    }

    @Override
    public String getMethod() {
        return httpMethod.name();
    }

    @Override
    public String getScheme() {
        return uri.getScheme() == null ? "" : uri.getScheme();
    }

    @Override
    public String getHost() {
        return uri.getHost() == null ? "" : uri.getHost();
    }

    @Override
    public Optional<Integer> getPort() {
        return Optional.of(uri).map(URI::getPort).filter(p -> p != -1);
    }

    @Override
    public String getPath() {
        return uri.getPath() == null ? "" : uri.getPath();
    }

    @Override
    public String getQuery() {
        return uri.getQuery() == null ? "" : uri.getQuery();
    }

    @Override
    public HttpRequest withBody() throws IOException {
        withBody = true;
        return this;
    }

    @Override
    public HttpRequest withoutBody() {
        withBody = false;
        return this;
    }
}
