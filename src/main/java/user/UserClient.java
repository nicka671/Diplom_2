package user;

import base.RestClient;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient {
    public static final String USER_CREATE = "api/auth/register";
    public static final String USER_LOGIN = "api/auth/login";
    public static final String USER_LOGOUT = "api/auth/logout";
    public static final String USER_MANAGE_DATA = "api/auth/user";

    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .when()
                .post(USER_CREATE)
                .then()
                .log().all();
    }

    public ValidatableResponse loginUser(UserLogin userLogin) {
        return given()
                .spec(getBaseSpec())
                .body(userLogin)
                .log().all()
                .when()
                .post(USER_LOGIN)
                .then()
                .log().all();
    }

    public ValidatableResponse logoutUser(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .log().all()
                .post(USER_LOGOUT)
                .then()
                .log().all();
    }

    public ValidatableResponse getUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(USER_MANAGE_DATA)
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .log().all()
                .delete(USER_MANAGE_DATA)
                .then();
    }

    public ValidatableResponse updateUserWithAuthorization(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .log().all()
                .patch(USER_MANAGE_DATA)
                .then();
    }

    public ValidatableResponse updateUserWithoutAuthorization(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .patch(USER_MANAGE_DATA)
                .then();
    }
}
