package io.quarkiverse.logbook.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class LogbookResourceTest {

    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/logbook")
                .then()
                .statusCode(200)
                .body(is("Hello logbook"));
    }
}
