package io.quarkiverse.logbook.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * JAX-RS resource for Logbook integration tests.
 * This resource provides several endpoints to test different aspects of Logbook's functionality,
 * such as basic request logging, error handling, client-side logging, and body obfuscation.
 */
@Path("/logbook")
@ApplicationScoped
public class LogbookResource {
    private final Logger log = Logger.getLogger(LogbookResource.class);

    private final HelloClient helloClient;

    /**
     * Constructs a new LogbookResource with an injected REST client.
     *
     * @param helloClient the {@link HelloClient} to be used for making outbound requests.
     */
    public LogbookResource(@RestClient HelloClient helloClient) {
        this.helloClient = helloClient;
    }

    /**
     * A simple endpoint that returns a plain text message.
     * Used to test basic request and response logging.
     *
     * @return a "Hello logbook" string.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        log.info("hello invoked");
        return "Hello logbook";
    }

    /**
     * An endpoint that always throws a {@link WebApplicationException}.
     * Used to test Logbook's handling of server-side errors.
     */
    @GET
    @Path("/error")
    public void error() {
        log.info("error invoked");
        throw new WebApplicationException("Boom!", new IllegalStateException("bomb"));
    }

    /**
     * An endpoint that always throws a {@link BadRequestException}.
     * Used to test Logbook's handling of specific HTTP error codes.
     */
    @GET
    @Path("/bad-request")
    public void badRequest() {
        log.info("badRequest invoked");
        throw new BadRequestException("bad boy", new IllegalArgumentException("bad argument"));
    }

    /**
     * An endpoint that uses a REST client to make an outbound request.
     * Used to test Logbook's logging of client-side requests and responses.
     *
     * @return a string indicating the client's response.
     */
    @GET
    @Path("/client")
    public String client() {
        log.info("client invoked");
        final var answer = helloClient.sayHello("TopSecret");
        log.infof("client received: %s", answer);
        return "Client answered: " + answer;
    }

    /**
     * An endpoint that consumes and produces a JSON object.
     * Used to test Logbook's handling of JSON request and response bodies, including obfuscation.
     *
     * @param dto the {@link JsonDto} received in the request body.
     * @return the same {@link JsonDto} that was received.
     */
    @POST
    @Path("/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonDto json(JsonDto dto) {
        log.info("json invoked");
        return dto;
    }

    /**
     * An endpoint that consumes form data.
     * Used to test Logbook's handling of form-encoded request bodies and parameter obfuscation.
     *
     * @param password a string from the "password" form field.
     * @return a confirmation message.
     */
    @POST
    @Path("/form")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String form(@FormParam("password") String password) {
        log.info("form invoked");
        return "secret submitted";
    }
}
