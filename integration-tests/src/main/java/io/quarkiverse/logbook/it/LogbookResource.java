package io.quarkiverse.logbook.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/logbook")
@ApplicationScoped
public class LogbookResource {
    private final Logger log = Logger.getLogger(LogbookResource.class);

    private final HelloClient helloClient;

    public LogbookResource(@RestClient HelloClient helloClient) {
        this.helloClient = helloClient;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        log.info("hello invoked");
        return "Hello logbook";
    }

    @GET
    @Path("/error")
    public void error() {
        log.info("error invoked");
        throw new WebApplicationException("Boom!", new IllegalStateException("bomb"));
    }

    @GET
    @Path("/bad-request")
    public void badRequest() {
        log.info("badRequest invoked");
        throw new BadRequestException("bad boy", new IllegalArgumentException("bad argument"));
    }

    @GET
    @Path("/client")
    public String client() {
        log.info("client invoked");
        final var answer = helloClient.sayHello("TopSecret");
        log.infof("client received: %s", answer);
        return "Client answered: " + answer;
    }
}
