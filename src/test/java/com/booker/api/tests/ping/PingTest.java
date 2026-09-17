package com.booker.api.tests.ping;

import com.booker.api.base.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PingTest extends BaseTest {

    @Test
    void pingReturns201() {
        Response response = apiClient.get("/ping");

        assertThat(response.getStatusCode()).isEqualTo(201);
    }
}
