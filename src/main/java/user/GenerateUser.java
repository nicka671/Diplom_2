package user;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
import user.User;

public class GenerateUser {
    public static User getUserData() {
        String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@nika.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name =  RandomStringUtils.randomAlphabetic(10);

        return new User(email, password, name);
    }

    public static User getUserDataOneFieldIsEmpty() {
        return new User("", "password_nika_3", "name_nika_3");
    }
}
