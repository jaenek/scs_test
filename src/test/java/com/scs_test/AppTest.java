package com.scs_test;

import java.util.HashMap;

import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;

/**
 * Unit test for Spring Currency Sample App.
 */
public class AppTest {
    public void testBasic(String endpoint, String schemaPath) {
        RestAssured.given().filter(new RestAssuredRequestFilter()).when().get(endpoint).then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath)).extract().response();
    }

    @Test
    public void testEndpoints() {
        HashMap<String, String> endpoints = new HashMap<String,String>();
        endpoints.put("/gold", "gold.json");
        endpoints.put("/health", "health.json");

        for (String endpoint : endpoints.keySet()) {
            testBasic(endpoint, endpoints.get(endpoint));
        }
    }

    @Test
    public void testMid() {
        RestAssured.given().filter(new RestAssuredRequestFilter()).when().queryParam("currency", "usd").get("/mid").then()
            .assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("currency.json"));
    }
}
