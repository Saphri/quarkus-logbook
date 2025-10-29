package io.quarkiverse.logbook.it;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * A REST client interface for the Logbook example, used to make HTTP requests to the LogbookResource.
 * This client is registered with a base URI of http://localhost:8081 and targets the "/logbook" path.
 */
@RegisterRestClient(baseUri = "http://localhost:8081")
@Path("/logbook")
public interface HelloClient {

    /**
     * Makes a GET request to the "/logbook" endpoint with a "secret" query parameter.
     * This method is used for testing Logbook's logging of client-side requests.
     *
     * @param secret a string that will be passed as a query parameter.
     * @return the response from the server as a plain text string.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String sayHello(@QueryParam("secret") String secret);
}
