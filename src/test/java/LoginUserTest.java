import org.junit.After;
import user.*;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class LoginUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        user = GenerateUser.getUserData();
        userClient = new UserClient();
        response = userClient.createUser(user);
    }

    @After
    public void tearDown()
    {
        ValidatableResponse response = userClient.deleteUser(accessToken);
        int statusCode = response.extract().statusCode();
        assertEquals("Код состояния должен быть 202", SC_ACCEPTED, statusCode);
    }

    @Test
    public void loginWithExistingUser() {
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        ValidatableResponse authResponse = userClient.loginUser(userLogin);
        int loginStatusCode = authResponse.extract().statusCode();
        assertEquals("Код состояния должен быть 200", SC_OK, loginStatusCode);
        boolean isCreated = authResponse.extract().path("success");
        assertTrue("Пользователь не зарегистрирован", isCreated);
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.split("Bearer ")[1];
        assertNotNull("accessToken не должен быть пустым", accessToken);
        String refreshToken = authResponse.extract().path("refreshToken");
        assertNotNull("refreshToken не должен быть пустым", refreshToken);
        String userEmail = authResponse.extract().path("user.email");
        assertEquals("Email в ответе от сервера не соответствует введённому при регистрации",
                user.getEmail(), userEmail);
        String userName = authResponse.extract().path("user.name");
        assertEquals("Email в ответе от сервера не соответствует введённому при регистрации",
                user.getName(), userName);
    }

    @Test
    public void loginWithIncorrectLoginAndPassword() {
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.split("Bearer ")[1];
        UserLogin userLogin = new UserLogin(user.getEmail() + "I", user.getPassword());
        ValidatableResponse authResponse = userClient.loginUser(userLogin);
        int loginStatusCode = authResponse.extract().statusCode();
        assertEquals("Код состояния должен быть 401", SC_UNAUTHORIZED, loginStatusCode);
        boolean isCreated = authResponse.extract().path("success");
        assertFalse("Пользователь смог залогиниться, введя некорректные логин и пароль", isCreated);
        System.out.println("success: " + isCreated);
        String message = authResponse.extract().path("message");
        System.out.println("message: " + message);
        assertEquals("Сообщение об ошибке некорректное","email or password are incorrect", message);
    }
}