package com.booker.api.client;

import com.booker.api.config.EnvironmentConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Low-level HTTP transport for the whole framework. Owns the single shared
 * {@link RequestSpecification} (base URI, content type, JSON mapper, Allure
 * reporting filter, request logging) and exposes plain HTTP-verb methods.
 * <p>
 * Nothing outside this class touches RestAssured's {@code given()} directly —
 * resource clients (e.g. {@link BookingApiClient}) compose this class rather
 * than extend a generic base client.
 */
public class ApiClient {

    private final RequestSpecification requestSpecification;

    public ApiClient(EnvironmentConfig config) {
        this.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(config.baseUri())
                .setContentType(ContentType.JSON)
                .setConfig(RestAssuredConfig.config()
                        .objectMapperConfig(new ObjectMapperConfig()
                                .jackson2ObjectMapperFactory((type, charset) -> buildObjectMapper())))
                .addFilter(new AllureRestAssured())
                .build();
    }

    public Response get(String path) {
        return given().spec(requestSpecification).log().ifValidationFails()
                .when().get(path);
    }

    public Response get(String path, Map<String, ?> queryParams) {
        return given().spec(requestSpecification).queryParams(queryParams).log().ifValidationFails()
                .when().get(path);
    }

    public Response post(String path, Object body) {
        return given().spec(requestSpecification).body(body).log().ifValidationFails()
                .when().post(path);
    }

    public Response put(String path, Object body, String cookieToken) {
        return withOptionalToken(given().spec(requestSpecification), cookieToken)
                .body(body).log().ifValidationFails()
                .when().put(path);
    }

    public Response patch(String path, Object body, String cookieToken) {
        return withOptionalToken(given().spec(requestSpecification), cookieToken)
                .body(body).log().ifValidationFails()
                .when().patch(path);
    }

    public Response delete(String path, String cookieToken) {
        return withOptionalToken(given().spec(requestSpecification), cookieToken)
                .log().ifValidationFails()
                .when().delete(path);
    }

    private static RequestSpecification withOptionalToken(RequestSpecification spec, String cookieToken) {
        return cookieToken == null ? spec : spec.cookie("token", cookieToken);
    }

    private static ObjectMapper buildObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
