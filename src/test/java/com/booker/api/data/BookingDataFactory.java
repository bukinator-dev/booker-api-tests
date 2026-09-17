package com.booker.api.data;

import com.booker.api.model.request.Booking;
import com.booker.api.model.request.BookingDates;

import java.time.LocalDate;

/**
 * Centralizes what a "valid" vs. "invalid" booking payload looks like for
 * this test suite, built on top of {@link Booking#builder()}. The factory
 * owns domain intent (positive/negative data); the builder owns assembly —
 * avoids duplicating literal JSON/POJO construction across test classes.
 */
public final class BookingDataFactory {

    private BookingDataFactory() {
    }

    public static Booking validBooking() {
        return validBooking(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
    }

    public static Booking validBooking(LocalDate checkin, LocalDate checkout) {
        return Booking.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin(checkin)
                        .checkout(checkout)
                        .build())
                .additionalneeds("Breakfast")
                .build();
    }

    public static Booking missingFirstnameBooking() {
        Booking booking = validBooking();
        booking.setFirstname(null);
        return booking;
    }

    public static Booking negativeTotalPriceBooking() {
        Booking booking = validBooking();
        booking.setTotalprice(-100);
        return booking;
    }
}
