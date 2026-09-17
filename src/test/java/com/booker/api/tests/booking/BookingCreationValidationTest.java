package com.booker.api.tests.booking;

import com.booker.api.base.BaseTest;
import com.booker.api.data.BookingDataFactory;
import com.booker.api.model.request.Booking;
import com.booker.api.model.response.BookingResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Positive and negative {@code POST /booking} payloads. Expected outcomes
 * below were verified against the live restful-booker demo API rather than
 * assumed: it has no request validation for a missing required field (500)
 * and is fully lenient about a negative total price (200, accepted as-is).
 */
class BookingCreationValidationTest extends BaseTest {

    private final List<Integer> createdBookingIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        String token = authTokenManager.getToken();
        for (int id : createdBookingIds) {
            bookingApiClient.deleteBooking(id, token);
        }
        createdBookingIds.clear();
    }

    @Test
    void canCreateValidBooking() {
        Response response = bookingApiClient.createBooking(BookingDataFactory.validBooking());
        assertThat(response.getStatusCode()).isEqualTo(200);

        BookingResponse bookingResponse = response.as(BookingResponse.class);
        createdBookingIds.add(bookingResponse.getBookingid());
        assertThat(bookingResponse.getBooking().getFirstname()).isEqualTo("Jim");
    }

    @Test
    void creatingBookingWithMissingFirstnameReturns500() {
        Booking booking = BookingDataFactory.missingFirstnameBooking();

        Response response = bookingApiClient.createBooking(booking);

        assertThat(response.getStatusCode()).isEqualTo(500);
    }

    @Test
    void creatingBookingWithNegativeTotalPriceIsAcceptedByApi() {
        Response response = bookingApiClient.createBooking(BookingDataFactory.negativeTotalPriceBooking());
        assertThat(response.getStatusCode()).isEqualTo(200);

        BookingResponse bookingResponse = response.as(BookingResponse.class);
        createdBookingIds.add(bookingResponse.getBookingid());
        assertThat(bookingResponse.getBooking().getTotalprice()).isEqualTo(-100);
    }
}
