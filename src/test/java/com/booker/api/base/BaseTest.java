package com.booker.api.base;

import com.booker.api.auth.AuthTokenManager;
import com.booker.api.client.ApiClient;
import com.booker.api.client.AuthApiClient;
import com.booker.api.client.BookingApiClient;
import com.booker.api.config.ConfigProvider;
import com.booker.api.config.EnvironmentConfig;
import org.junit.jupiter.api.BeforeAll;

/**
 * Shared JUnit lifecycle for every test class in the suite. This is the one
 * deliberate inheritance ({@code is-a}) relationship in the framework: every
 * concrete test class extends this to get the client graph wired once per
 * suite, instead of duplicating {@code @BeforeAll} setup everywhere.
 */
public abstract class BaseTest {

    protected static EnvironmentConfig config;
    protected static ApiClient apiClient;
    protected static BookingApiClient bookingApiClient;
    protected static AuthApiClient authApiClient;
    protected static AuthTokenManager authTokenManager;

    @BeforeAll
    static void initSuite() {
        config = ConfigProvider.get();
        apiClient = new ApiClient(config);
        bookingApiClient = new BookingApiClient(apiClient);
        authApiClient = new AuthApiClient(apiClient);
        authTokenManager = new AuthTokenManager(authApiClient, config);
    }
}
