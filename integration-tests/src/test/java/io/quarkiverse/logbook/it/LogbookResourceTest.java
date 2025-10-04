package io.quarkiverse.logbook.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LogbookResourceTest {

    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/logbook")
                .then()
                .statusCode(200)
                .body(is("Hello logbook"));
    }

    @Test
    void testBadRequest() {
        given()
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
