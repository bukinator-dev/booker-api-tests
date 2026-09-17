package com.booker.api.client;

import com.booker.api.model.request.AuthRequest;
import io.restassured.response.Response;

/**
 * Maps the {@code /auth} resource to domain-shaped operations. Composes
 * {@link ApiClient} rather than extending it.
 */
public class AuthApiClient {

    private static final String AUTH_PATH = "/auth";

    private final ApiClient apiClient;

    public AuthApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response createToken(AuthRequest authRequest) {
        return apiClient.post(AUTH_PATH, authRequest);
    }
}
