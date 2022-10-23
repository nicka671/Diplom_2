import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.*;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class UpdateUserTest {
    private User user;
    private User userWithUpdatedData;
    private UserClient userClient;
    private String accessToken;
    private ValidatableResponse response;
    private ValidatableResponse responseLogin;
    public static final String UPDATE_UNAUTHORIZED = "You should be authorised";


    @Before
    public void setUp() {
        user = GenerateUser.getUserData();
        userClient = new UserClient();
    }

    @After
    public void tearDown()
    {
        accessToken = accessToken.split("Bearer ")[1];
        response = userClient.deleteUser(accessToken);
        int statusCode = response.extract().statusCode();
        assertEquals("Код состояния должен быть 202", SC_ACCEPTED, statusCode);
    }

    @Test
    public void updateUserDataWithAuth() {
        response = userClient.createUser(user);
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        response = userClient.loginUser(userLogin);
        accessToken = response.extract().path("accessToken");
        String userUpdateEmail = user.getEmail() + "new";
        String userUpdateName = user.getName() + "new";
        user.setEmail(userUpdateEmail);
        user.setName(userUpdateName);
        ValidatableResponse responseUpdate = userClient.updateUserWithAuthorization(user, accessToken);
        int statusCode = responseUpdate.extract().statusCode();
        assertEquals("Код состояния должен быть 200", SC_OK, statusCode);
        String actualEmail = responseUpdate.extract().path("user.email");
        assertEquals("E-mail в ответе сервера не совпадает актуальным", userUpdateEmail, actualEmail);
        String actualName = responseUpdate.extract().path("user.name");
        assertEquals("E-mail в ответе сервера не совпадает актуальным", userUpdateName, actualName);
    }

    @Test
    public void updateUserDataWithoutAuth() {
        response = userClient.createUser(user);
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        response = userClient.loginUser(userLogin);
        accessToken = response.extract().path("accessToken");
        String userUpdateEmail = user.getEmail() + "new";
        String userUpdateName = user.getName() + "new";
        user.setEmail(userUpdateEmail);
        user.setName(userUpdateName);
        ValidatableResponse responseUpdate = userClient.updateUserWithoutAuthorization(user);
        int statusCode = responseUpdate.extract().statusCode();
        assertEquals("Код состояния должен быть 401", SC_UNAUTHORIZED, statusCode);
        boolean isUpdated = responseUpdate.extract().path("success");
        assertFalse("Удалось обновить пользовательские данные без авторизации", isUpdated);
        String actualMessage = responseUpdate.extract().path("message");
        System.out.println();
        assertEquals("Некорректное сообщение об обновлении данных неавторизоавнным пользователем", UPDATE_UNAUTHORIZED, actualMessage);
    }

}
