package edu.praktikum.diplom;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import io.qameta.allure.junit4.DisplayName;

public class UserOrdersTests {
    private UserSteps userSteps;
    private OrderSteps orderSteps;
    private String userToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
    }

    private void registerAndLoginUser() {
        // Генерация случайного пользователя
        User newUser = User.generateRandomUser();

        // Регистрация нового пользователя
        Response registerResponse = userSteps.createUser(newUser);
        Assert.assertEquals(200, registerResponse.getStatusCode());

        // Логин для получения токена
        Response loginResponse = userSteps.loginUser(newUser.getEmail(), newUser.getPassword());
        Assert.assertEquals(200, loginResponse.getStatusCode());
        userToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    public void testGetOrdersWithAuthorization() {
        registerAndLoginUser();

        Response ordersResponse = orderSteps.getOrders(userToken);
        System.out.println("Orders Response: " + ordersResponse.getBody().asString());

        Assert.assertEquals(200, ordersResponse.getStatusCode());
        Assert.assertTrue(ordersResponse.getBody().asString().contains("success"));
        Assert.assertTrue(ordersResponse.getBody().asString().contains("orders"));
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void testGetOrdersWithoutAuthorization() {
        Response ordersResponse = orderSteps.getOrders(null);
        System.out.println("Orders Response: " + ordersResponse.getBody().asString());

        Assert.assertEquals(401, ordersResponse.getStatusCode());
        Assert.assertTrue(ordersResponse.getBody().asString().contains("You should be authorised"));
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
