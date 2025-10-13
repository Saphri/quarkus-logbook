package io.quarkiverse.logbook.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(OffTestProfile.class)
public class OffFormRequestModeTest {

    @Test
    void testOff() {
        given()
                .formParam("param1", "value1")
                .formParam("param2", "value2")
                .when().post("/test/form")
                .then()
                .statusCode(200)
                .body(is("param1=value1,param2=value2"));
    }
}