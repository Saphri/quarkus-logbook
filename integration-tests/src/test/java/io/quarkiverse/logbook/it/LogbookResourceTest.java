package io.quarkiverse.logbook.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LogbookResourceTest {

    private static final String SECRET = "TopSecret";

    @Test
    void testHelloEndpoint() {
        given()
                .headers(Map.of("Authorization", SECRET))
                .accept(MediaType.TEXT_PLAIN)
                .when()
                .get("/logbook")
                .then()
                .statusCode(200)
                .body(is("Hello logbook"));
    }

    @Test
    void testBadRequest() {
        given()
                .queryParam("access_token", SECRET)
                .when().get("/logbook/bad-request")
                .then()
                .statusCode(400);
    }

    @Test
    void testError() {
        given()
                .when().get("/logbook/error")
                .then()
                .statusCode(500);
    }
}
