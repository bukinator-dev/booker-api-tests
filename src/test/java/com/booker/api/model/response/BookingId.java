package com.booker.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single element of the {@code GET /booking} id list response, e.g. {"bookingid": 1}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingId {

    private int bookingid;
}
