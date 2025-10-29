package io.quarkiverse.logbook.it;

/**
 * A simple data transfer object (DTO) used for testing JSON request and response logging.
 * This record is used in the LogbookResource to demonstrate how Logbook handles JSON bodies.
 *
 * @param message a non-sensitive message string.
 * @param secret a sensitive string that should be obfuscated in the logs.
 */
public record JsonDto(String message, String secret) {
}