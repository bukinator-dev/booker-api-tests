import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingApiTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    @Test
    void pingReturn201(){
        given()
                .log().uri()
                .when()
                .get("/ping")
                .then()
                .statusCode(201);
    }

    @Test
    void canGetBookList(){
        Response reponse = given()
                .when()
                .get("/booking");
        assertEquals(200, reponse.getStatusCode());
        System.out.println(reponse.jsonPath().getList("booking"));
    }

    @Test
    void canCreateBooking(){
        String body = """
                {
                    "firstname" : "Jim",
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }
                """;
        given().contentType("application/json")
                .body(body)
                .log().all(true)
                .when()
                .post("/booking")
                .then()
                .statusCode(200);
    }
}
