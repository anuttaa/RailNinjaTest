package org.example;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Specifications {
    private static final String API_KEY = "4ae3369b0952f1c1176deec94708f3a7";

    public static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://back.rail.ninja/api/v2")
            .setContentType(ContentType.JSON)
            .addHeader("Accept", "application/json")
            .addHeader("X-currency", "USD")
            .addHeader("X-API-User-Key", API_KEY)
            .build();

    public static ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .build();
}
