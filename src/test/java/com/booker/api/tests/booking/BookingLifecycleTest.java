package com.booker.api.tests.booking;

import com.booker.api.base.BaseTest;
import com.booker.api.data.BookingDataFactory;
import com.booker.api.model.request.Booking;
import com.booker.api.model.response.BookingResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full create -&gt; get -&gt; update -&gt; partial update -&gt; delete -&gt; verify-gone
 * lifecycle for a single booking. The booking is deleted as the final step
 * of the test itself, so no separate {@code @AfterEach} cleanup is needed here.
 */
class BookingLifecycleTest extends BaseTest {

    @Test
    void fullBookingLifecycle() {
        Booking booking = BookingDataFactory.validBooking();
        Response createResponse = bookingApiClient.createBooking(booking);
        assertThat(createResponse.getStatusCode()).isEqualTo(200);
        int bookingId = createResponse.as(BookingResponse.class).getBookingid();

        Response getResponse = bookingApiClient.getBookingById(bookingId);
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        assertThat(getResponse.as(Booking.class).getFirstname()).isEqualTo("Jim");

        String token = authTokenManager.getToken();

        Booking updatedBooking = BookingDataFactory.validBooking(
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 10));
        updatedBooking.setFirstname("James");
        Response putResponse = bookingApiClient.updateBooking(bookingId, updatedBooking, token);
        assertThat(putResponse.getStatusCode()).isEqualTo(200);
        assertThat(putResponse.as(Booking.class).getFirstname()).isEqualTo("James");

        Response patchResponse = bookingApiClient.partialUpdateBooking(bookingId, Map.of("lastname", "Doe"), token);
        assertThat(patchResponse.getStatusCode()).isEqualTo(200);
        assertThat(patchResponse.as(Booking.class).getLastname()).isEqualTo("Doe");

        Response deleteResponse = bookingApiClient.deleteBooking(bookingId, token);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(201);

        Response verifyResponse = bookingApiClient.getBookingById(bookingId);
        assertThat(verifyResponse.getStatusCode()).isEqualTo(404);
    }
}
