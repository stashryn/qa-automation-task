package com.qa.automation.api.client;

import com.qa.automation.api.model.Booking;
import com.qa.automation.config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/** Sends requests and returns the raw Response ,аssertions are done in the tests */
public class BookingClient {

    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(TestConfig.get("api.base.url"))
            .setContentType(ContentType.JSON)
            .setAccept("application/json") 
            .setConfig(RestAssuredConfig.config().logConfig(
                    LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
            .build();

    public Response createBooking(Booking booking) {
        return given().spec(spec)
                .body(booking)
                .post("/booking");
    }

    public Response getBooking(long id) {
        return given().spec(spec)
                .get("/booking/{id}", id);
    }

    public Response deleteBooking(long id) {
        return given().spec(spec)
                .auth().preemptive().basic(TestConfig.get("api.admin.username"), TestConfig.get("api.admin.password"))
                .delete("/booking/{id}", id);
    }

    public Response deleteBookingWithoutAuth(long id) {
        return given().spec(spec)
                .delete("/booking/{id}", id);
    }
}
