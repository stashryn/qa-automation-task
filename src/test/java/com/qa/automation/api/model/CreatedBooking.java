package com.qa.automation.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// response of POST /booking
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreatedBooking(long bookingid, Booking booking) {
}
