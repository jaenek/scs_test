package com.scs_test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.module.jsv.JsonSchemaValidator;

/**
 * Unit test for Spring Currency Sample App.
 */
public class AppTest {
    RequestSpecification requestSpecification;

    @Rule
    public ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("log.txt");
            } catch (FileNotFoundException ex) {
                System.out.println("log.txt is a directory!");
                System.exit(1);
            }
            PrintStream log = new PrintStream(fos);

            requestSpecification = new RequestSpecBuilder().setBaseUri("http://localhost:8080")
                    .addFilter(RequestLoggingFilter.logRequestTo(log))
                    .addFilter(ResponseLoggingFilter.logResponseTo(log)).build();
        }
    };

    public void testBasic(String endpoint, String schemaPath) {
        RestAssured.given().spec(requestSpecification).when().get(endpoint).then().assertThat().statusCode(200).body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath)).log()
                .all();
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
        RestAssured.given().spec(requestSpecification).when().queryParam("currency", "usd").get("/mid").then()
                .assertThat().statusCode(200).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("currency.json")).log().all();
    }
}
