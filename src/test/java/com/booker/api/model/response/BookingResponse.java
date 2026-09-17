package com.booker.api.model.response;

import com.booker.api.model.request.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload for {@code POST /booking}: the newly created id plus the echoed booking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private int bookingid;
    private Booking booking;
}
