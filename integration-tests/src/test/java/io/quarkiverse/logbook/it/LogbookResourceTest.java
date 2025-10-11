package io.quarkiverse.logbook.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import jakarta.ws.rs.core.MediaType;

import org.jboss.logmanager.LogManager;
import org.junit.jupiter.api.Test;
import org.zalando.logbook.Logbook;

import io.quarkus.test.InMemoryLogHandler;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LogbookResourceTest {

    private static final String SECRET = "TopSecret";
    private static final java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
    private static final InMemoryLogHandler inMemoryLogHandler = new InMemoryLogHandler(
            r -> r.getLoggerName().equals(Logbook.class.getName()));

    static {
        rootLogger.addHandler(inMemoryLogHandler);
    }

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

    @Test
    void testViaClient() {
        given()
                .accept(MediaType.TEXT_PLAIN)
                .when()
                .get("/logbook/client")
                .then()
                .statusCode(200)
                .body(is("Client answered: Hello logbook"));
    }

    @Test
    void testJson() {
        inMemoryLogHandler.setFilter(
                logRecord -> logRecord.getLoggerName().equals(Logbook.class.getName()));
        inMemoryLogHandler.setLevel(Level.ALL);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"message":"Hello json","secret":"super-secret"}""")
                .when().post("/logbook/json")
                .then()
                .statusCode(200)
                .body("message", is("Hello json"))
                .body("secret", is("super-secret"));

        List<LogRecord> logRecords = inMemoryLogHandler.getRecords();
        assertThat(logRecords).anyMatch(logRecord -> logRecord.getMessage().contains(
                "{\"message\":\"Hello json\",\"secret\":\"XXX\"}"));
    }
}
