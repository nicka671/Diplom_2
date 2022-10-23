import org.junit.After;
import user.*;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateUserTest {
    private User uniqueUser;
    private User oneFieldIsEmptyUser;
    private UserClient userClient;
    private String accessToken;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        uniqueUser = GenerateUser.getUserData();
        userClient = new UserClient();
        oneFieldIsEmptyUser = GenerateUser.getUserDataOneFieldIsEmpty();
        response = userClient.createUser(uniqueUser);
    }

    @After
    public void tearDown()
    {
        ValidatableResponse response = userClient.deleteUser(accessToken);
        int statusCode = response.extract().statusCode();
        assertEquals("Код состояния должен быть 202", SC_ACCEPTED, statusCode);
    }


    @Test
    public void createUniqueUser() {
        //ValidatableResponse response = userClient.createUser(uniqueUser);
        int statusCode = response.extract().statusCode();
        assertEquals("Код состояния должен быть 200", SC_OK, statusCode);
        boolean isCreated = response.extract().path("success");
        assertTrue("Пользователь не зарегистрирован", isCreated);
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.split("Bearer ")[1];
        assertNotNull("accessToken не должен быть пустым", accessToken);
        String refreshToken = response.extract().path("refreshToken");
        assertNotNull("refreshToken не должен быть пустым", refreshToken);
        String userEmail = response.extract().path("user.email");
        assertEquals("Email в ответе от сервера не соответствует введённому при регистрации",
                              uniqueUser.getEmail(), userEmail);
        String userName = response.extract().path("user.name");
        assertEquals("Email в ответе от сервера не соответствует введённому при регистрации",
                uniqueUser.getName(), userName);
        UserLogin userLogin = new UserLogin(uniqueUser.getEmail(), uniqueUser.getPassword());
        ValidatableResponse authResponse = userClient.loginUser(userLogin

        );
        int loginStatusCode = authResponse.extract().statusCode();
        assertEquals("Код состояния должен быть 200", SC_OK, loginStatusCode);
    }

    @Test
    public void createUserWhichIsAlreadyCreated() {
        //ValidatableResponse response = userClient.createUser(uniqueUser);
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.split("Bearer ")[1];
        ValidatableResponse responseAgain =  userClient.createUser(uniqueUser);
        int statusCode = responseAgain.extract().statusCode();
        assertEquals("Код состояния должен быть 403", SC_FORBIDDEN, statusCode);
        boolean isCreated = responseAgain.extract().path("success");
        assertFalse("Такой пользователь уже создан и не может быть зарегистрирован повторно", isCreated);
        String message = responseAgain.extract().path("message");
        assertEquals("Сообщение об ошибке некорректное","User already exists", message);
    }

    @Test
    public void createUserWithOneFieldIsEmpty() {
        ValidatableResponse responseIsEmptyUser = userClient.createUser(oneFieldIsEmptyUser);
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.split("Bearer ")[1];
        int statusCode = responseIsEmptyUser.extract().statusCode();
        assertEquals("Код состояния должен быть 403", SC_FORBIDDEN, statusCode);
        boolean isCreated = responseIsEmptyUser.extract().path("success");
        assertFalse("В данных пользователя одно из обязательных полей пустое", isCreated);
        String message = responseIsEmptyUser.extract().path("message");
        assertEquals("Сообщение об ошибке некорректное","Email, password and name are required fields", message);
    }
}