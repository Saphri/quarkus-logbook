package io.quarkiverse.logbook.it;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8081")
@Path("/logbook")
public interface HelloClient {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String sayHello(@QueryParam("secret") String secret);
}
