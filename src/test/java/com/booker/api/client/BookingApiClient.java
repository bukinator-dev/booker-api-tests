package com.booker.api.client;

import com.booker.api.model.request.Booking;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Maps the {@code /booking} resource to domain-shaped operations. Composes
 * {@link ApiClient} rather than extending it.
 */
public class BookingApiClient {

    private static final String BOOKING_PATH = "/booking";
    private static final String BOOKING_BY_ID_PATH = "/booking/{id}";

    private final ApiClient apiClient;

    public BookingApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getAllBookingIds() {
        return apiClient.get(BOOKING_PATH);
    }

    public Response getBookingIds(Map<String, ?> filters) {
        return apiClient.get(BOOKING_PATH, filters);
    }

    public Response getBookingById(int id) {
        return apiClient.get(BOOKING_BY_ID_PATH.replace("{id}", String.valueOf(id)));
    }

    public Response createBooking(Booking booking) {
        return apiClient.post(BOOKING_PATH, booking);
    }

    public Response updateBooking(int id, Booking booking, String token) {
        return apiClient.put(BOOKING_BY_ID_PATH.replace("{id}", String.valueOf(id)), booking, token);
    }

    public Response partialUpdateBooking(int id, Map<String, ?> fields, String token) {
        return apiClient.patch(BOOKING_BY_ID_PATH.replace("{id}", String.valueOf(id)), fields, token);
    }

    public Response deleteBooking(int id, String token) {
        return apiClient.delete(BOOKING_BY_ID_PATH.replace("{id}", String.valueOf(id)), token);
    }
}
