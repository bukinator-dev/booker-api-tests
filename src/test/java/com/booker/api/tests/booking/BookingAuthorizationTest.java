package com.booker.api.tests.booking;

import com.booker.api.base.BaseTest;
import com.booker.api.data.BookingDataFactory;
import com.booker.api.model.response.BookingResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PUT/PATCH/DELETE are only accepted with a valid {@code /auth} token cookie.
 * These tests confirm the API rejects them (403) with no token and with an
 * invalid token, verified live against the demo API before writing assertions.
 */
class BookingAuthorizationTest extends BaseTest {

    private int bookingId;

    @BeforeEach
    void createBooking() {
        Response response = bookingApiClient.createBooking(BookingDataFactory.validBooking());
        bookingId = response.as(BookingResponse.class).getBookingid();
    }

    @AfterEach
    void cleanup() {
        bookingApiClient.deleteBooking(bookingId, authTokenManager.getToken());
    }

    @Test
    void updateWithoutTokenReturns403() {
        Response response = bookingApiClient.updateBooking(bookingId, BookingDataFactory.validBooking(), null);

        assertThat(response.getStatusCode()).isEqualTo(403);
    }

    @Test
    void partialUpdateWithoutTokenReturns403() {
        Response response = bookingApiClient.partialUpdateBooking(bookingId, Map.of("lastname", "Hacker"), null);

        assertThat(response.getStatusCode()).isEqualTo(403);
    }

    @Test
    void deleteWithoutTokenReturns403() {
        Response response = bookingApiClient.deleteBooking(bookingId, null);

        assertThat(response.getStatusCode()).isEqualTo(403);
    }

    @Test
    void deleteWithInvalidTokenReturns403() {
        Response response = bookingApiClient.deleteBooking(bookingId, "not-a-real-token");

        assertThat(response.getStatusCode()).isEqualTo(403);
    }
}
