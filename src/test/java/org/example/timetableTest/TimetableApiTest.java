package org.example.timetableTest;
import org.example.Specifications;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class TimetableApiTest {

    @Test
    public void testTimetablePostWithLegs() {
        String requestBody = """
                {
                  "passengers": { "adults": 1, "children": 0, "children_age": [] },
                  "legs": {
                    "1": {
                      "departure_station": "23e9ca21-c51d-41be-b421-94e2da736ce3",
                      "arrival_station": "8fbfe521-8d0c-4187-9076-ad1731b42ae9",
                      "departure_date": "05.11.2025"
                    }
                  }
                }
                """;

        TimetableResponse response = given()
                .spec(Specifications.requestSpec)
                .body(requestBody)
                .when()
                .post("/timetable")
                .then()
                .spec(Specifications.responseSpec)
                .extract()
                .as(TimetableResponse.class);

        assertNotNull(response.getDepartureStation());
        assertNotNull(response.getArrivalStation());
        assertNotNull(response.getTrains());
        assertFalse(response.getTrains().isEmpty());

        response.getTrains().values().forEach(train -> {
            assertEquals("Mecca station", train.getDepartureStation().getSingleName());
            assertEquals("Medina station", train.getArrivalStation().getSingleName());
            assertTrue(train.getDepartureDatetime().startsWith("2025-11-05"));
        });
    }
}
