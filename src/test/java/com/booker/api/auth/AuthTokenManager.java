package com.booker.api.auth;

import com.booker.api.client.AuthApiClient;
import com.booker.api.config.EnvironmentConfig;
import com.booker.api.model.request.AuthRequest;
import com.booker.api.model.response.AuthResponse;
import io.restassured.response.Response;

/**
 * Lazily fetches and caches the {@code /auth} token for the running suite, so
 * every test that needs an authenticated request (PUT/PATCH/DELETE) shares
 * one token instead of re-authenticating per test.
 */
public class AuthTokenManager {

    private final AuthApiClient authApiClient;
    private final EnvironmentConfig config;
    private String cachedToken;

    public AuthTokenManager(AuthApiClient authApiClient, EnvironmentConfig config) {
        this.authApiClient = authApiClient;
        this.config = config;
    }

    public String getToken() {
        if (cachedToken == null) {
            AuthRequest request = AuthRequest.builder()
                    .username(config.authUsername())
                    .password(config.authPassword())
                    .build();

            Response response = authApiClient.createToken(request);
            AuthResponse authResponse = response.as(AuthResponse.class);
            if (authResponse.getToken() == null) {
                throw new IllegalStateException("Failed to obtain auth token: " + response.getBody().asString());
            }
            cachedToken = authResponse.getToken();
        }
        return cachedToken;
    }

    public void invalidate() {
        cachedToken = null;
    }
}
