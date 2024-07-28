package edu.praktikum.diplom;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserLoginTests {
    private UserSteps userSteps;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        userSteps = new UserSteps();
    }

    @Test
    @DisplayName("Логин с валидными учетными данными")
    public void testLoginWithValidCredentials() {
        // Генерация случайного пользователя
        User newUser = User.generateRandomUser();

        // Регистрация нового пользователя
        Response registerResponse = userSteps.createUser(newUser);
        Assert.assertEquals(200, registerResponse.getStatusCode());

        // Логин с валидными учетными данными
        Response loginResponse = userSteps.loginUser(newUser.getEmail(), newUser.getPassword());
        System.out.println("Login Response: " + loginResponse.getBody().asString());

        Assert.assertEquals(200, loginResponse.getStatusCode());
        Assert.assertTrue(loginResponse.getBody().asString().contains("success"));
    }

    @Test
    @DisplayName("Логин с неверными учетными данными")
    public void testLoginWithInvalidCredentials() {
        // Генерация случайных невалидных данных
        String invalidEmail = "invalid@example.com";
        String invalidPassword = "invalidPassword";

        // Логин с невалидными учетными данными
        Response loginResponse = userSteps.loginUser(invalidEmail, invalidPassword);
        System.out.println("Login Response: " + loginResponse.getBody().asString());

        Assert.assertEquals(401, loginResponse.getStatusCode());
        Assert.assertTrue(loginResponse.getBody().asString().contains("email or password are incorrect"));
    }
}