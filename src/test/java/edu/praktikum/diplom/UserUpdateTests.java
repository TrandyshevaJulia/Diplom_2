package edu.praktikum.diplom;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

public class UserUpdateTests {
    private UserSteps userSteps;
    private String userToken;
    private User newUser;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        userSteps = new UserSteps();

        // Генерация случайного пользователя
        newUser = User.generateRandomUser();

        // Регистрация нового пользователя
        Response registerResponse = userSteps.createUser(newUser);
        Assert.assertEquals(200, registerResponse.getStatusCode());

        // Логин для получения токена
        Response loginResponse = userSteps.loginUser(newUser.getEmail(), newUser.getPassword());
        Assert.assertEquals(200, loginResponse.getStatusCode());
        userToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void testUpdateUserWithAuthorization() {
        // Генерация новых данных для обновления
        User updatedUser = User.generateRandomUser();

        // Обновление данных пользователя с авторизацией
        Response updateResponse = userSteps.updateUser(userToken, updatedUser);
        System.out.println("Update Response: " + updateResponse.getBody().asString());

        Assert.assertEquals(200, updateResponse.getStatusCode());
        Assert.assertTrue(updateResponse.getBody().asString().contains("success"));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void testUpdateUserWithoutAuthorization() {
        // Генерация новых данных для обновления
        User updatedUser = User.generateRandomUser();

        // Обновление данных пользователя без авторизации
        Response updateResponse = userSteps.updateUser(null, updatedUser);
        System.out.println("Update Response: " + updateResponse.getBody().asString());

        Assert.assertEquals(401, updateResponse.getStatusCode());
        Assert.assertTrue(updateResponse.getBody().asString().contains("You should be authorised"));
    }

    @After
    public void tearDown() {
        // Удаление пользователя после теста
        if (userToken != null) {
            Response deleteResponse = userSteps.deleteUser(userToken);
            Assert.assertEquals(202, deleteResponse.getStatusCode());
        }
    }
}