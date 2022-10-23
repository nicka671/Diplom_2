package order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

import base.RestClient;

public class OrderClient extends RestClient {

    public static final String ORDER = "api/orders";
    public static final String GET_ALL_INGREDIENTS = "api/ingredients";
    public static final String GET_ALL_ORDERS = "api/orders/all";

    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(GET_ALL_ORDERS)
                .then()
                .log().all();
    }

    public ValidatableResponse getOrdersByAuthorization(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(ORDER)
                .then()
                .log().all();
    }

    public ValidatableResponse getOrdersWithoutAuthorization() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(ORDER)
                .then()
                .log().all();
    }

    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .get(GET_ALL_ORDERS)
                .then()
                .log().all();
    }

    public ValidatableResponse createOrderByAuthorization(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .log().all()
                .post(ORDER)
                .then()
                .log().all();
    }

    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .log().all()
                .post(ORDER)
                .then()
                .log().all();
    }
}
