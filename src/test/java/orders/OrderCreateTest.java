package orders;

import io.restassured.response.ValidatableResponse;
import order.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import user.*;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrderCreateTest {
    //private static final String MESSAGE_BAD_REQUEST = "Ingredient ids must be provided";
    private ValidatableResponse response;
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        user = GenerateUser.getUserData();
        order = new Order();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    public void orderCreateByAuthorization() {
        fillListIngredients();
        response = userClient.createUser(user);
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        response = userClient.loginUser(userLogin);
        String accessToken = response.extract().path("accessToken");
        response = orderClient.createOrderByAuthorization(order, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");
        String orderId = response.extract().path("order._id");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order is create incorrect", isCreate, equalTo(true));
        assertThat("Order number is null", orderNumber, notNullValue());
        assertThat("Order id is null", orderId, notNullValue());
    }

    private void fillListIngredients() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(0));
        ingredients.add(list.get(1));
        ingredients.add(list.get(0));
    }
}
