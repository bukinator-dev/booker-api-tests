package com.booker.api.tests.booking;

import com.booker.api.base.BaseTest;
import com.booker.api.data.BookingDataFactory;
import com.booker.api.model.request.Booking;
import com.booker.api.model.response.BookingId;
import com.booker.api.model.response.BookingResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRetrievalTest extends BaseTest {

    private int createdBookingId;

    @BeforeEach
    void createBookingForRetrieval() {
        Response response = bookingApiClient.createBooking(BookingDataFactory.validBooking());
        createdBookingId = response.as(BookingResponse.class).getBookingid();
    }

    @AfterEach
    void cleanup() {
        bookingApiClient.deleteBooking(createdBookingId, authTokenManager.getToken());
    }

    @Test
    void canListAllBookingIds() {
        Response response = bookingApiClient.getAllBookingIds();
        assertThat(response.getStatusCode()).isEqualTo(200);

        List<BookingId> ids = List.of(response.as(BookingId[].class));
        assertThat(ids).extracting(BookingId::getBookingid).contains(createdBookingId);
    }

    @Test
    void canFilterBookingIdsByName() {
        Response response = bookingApiClient.getBookingIds(Map.of("firstname", "Jim", "lastname", "Brown"));
        assertThat(response.getStatusCode()).isEqualTo(200);

        List<BookingId> ids = List.of(response.as(BookingId[].class));
        assertThat(ids).extracting(BookingId::getBookingid).contains(createdBookingId);
    }

    @Test
    void canGetBookingById() {
        Response response = bookingApiClient.getBookingById(createdBookingId);
        assertThat(response.getStatusCode()).isEqualTo(200);

        Booking booking = response.as(Booking.class);
        assertThat(booking.getFirstname()).isEqualTo("Jim");
        assertThat(booking.getLastname()).isEqualTo("Brown");
    }

    @Test
    void getBookingByInvalidIdReturns404() {
        Response response = bookingApiClient.getBookingById(Integer.MAX_VALUE);

        assertThat(response.getStatusCode()).isEqualTo(404);
    }
}
