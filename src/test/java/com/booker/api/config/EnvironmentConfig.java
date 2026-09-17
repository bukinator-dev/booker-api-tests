package com.booker.api.config;

/**
 * Immutable, typed view of the environment the test suite runs against.
 */
public record EnvironmentConfig(String baseUri, String authUsername, String authPassword, int timeoutMs) {
}
