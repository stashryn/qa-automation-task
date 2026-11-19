package com.qa.automation.api;

import com.qa.automation.api.client.BookingClient;
import com.qa.automation.api.model.Booking;
import com.qa.automation.api.model.BookingDates;
import com.qa.automation.api.model.CreatedBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
class BookingApiTest {

    private final BookingClient client = new BookingClient();
    private final List<Long> createdIds = new ArrayList<>();

    @AfterEach
    void deleteCreatedBookings() {
        createdIds.forEach(client::deleteBooking);
    }

    @Test
    void createdBookingCanBeReadBack() {
        Booking booking = newBooking();

        Response createResponse = client.createBooking(booking);
        createResponse.then().statusCode(200);
        CreatedBooking created = createResponse.as(CreatedBooking.class);
        createdIds.add(created.bookingid());

        assertThat(created.bookingid()).isPositive();
        assertThat(created.booking()).isEqualTo(booking);

        Response getResponse = client.getBooking(created.bookingid());
        getResponse.then().statusCode(200);
        assertThat(getResponse.as(Booking.class)).isEqualTo(booking);
    }

    @Test
    void unknownBookingReturns404() {
        client.getBooking(Integer.MAX_VALUE).then().statusCode(404);
    }


    @Test
    void bookingCannotBeDeletedWithoutAuth() {
        long id = createBooking(newBooking());

        client.deleteBookingWithoutAuth(id).then().statusCode(403);

        client.getBooking(id).then().statusCode(200);
    }


    private long createBooking(Booking booking) {
        Response response = client.createBooking(booking);
        response.then().statusCode(200);
        long id = response.as(CreatedBooking.class).bookingid();
        createdIds.add(id);
        return id;
    }



    // unique names, so a test never depends on bookings from other tests or runs
    private static Booking newBooking() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        LocalDate checkin = LocalDate.now().plusDays(14);
        return new Booking(
                "John" + suffix,
                "Smith" + suffix,
                250,
                true,
                new BookingDates(checkin.toString(), checkin.plusDays(3).toString()),
                "Breakfast");
    }
}
