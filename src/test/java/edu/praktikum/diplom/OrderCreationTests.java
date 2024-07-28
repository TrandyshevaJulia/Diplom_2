package edu.praktikum.diplom;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderCreationTests {
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
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    public void testCreateOrderWithAuthorizationAndIngredients() {
        registerAndLoginUser();
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6e"); // Пример ингредиентов
        Order order = new Order(ingredients);

        Response orderResponse = orderSteps.createOrder(userToken, order);
        System.out.println("Order Response: " + orderResponse.getBody().asString());

        Assert.assertEquals(200, orderResponse.getStatusCode());
        Assert.assertTrue(orderResponse.getBody().asString().contains("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и с ингредиентами")
    public void testCreateOrderWithoutAuthorizationAndIngredients() {
        registerAndLoginUser();
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6e"); // Пример ингредиентов
        Order order = new Order(ingredients);

        Response orderResponse = orderSteps.createOrder(null, order);
        System.out.println("Order Response: " + orderResponse.getBody().asString());

        Assert.assertEquals(200, orderResponse.getStatusCode());
        Assert.assertTrue(orderResponse.getBody().asString().contains("success"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    public void testCreateOrderWithAuthorizationAndWithoutIngredients() {
        registerAndLoginUser();
        List<String> ingredients = Collections.emptyList(); // Пустой список ингредиентов
        Order order = new Order(ingredients);

        Response orderResponse = orderSteps.createOrder(userToken, order);
        System.out.println("Order Response: " + orderResponse.getBody().asString());

        Assert.assertEquals(400, orderResponse.getStatusCode()); // Ожидается ошибка
        Assert.assertTrue(orderResponse.getBody().asString().contains("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void testCreateOrderWithoutAuthorizationAndWithoutIngredients() {
        registerAndLoginUser();
        List<String> ingredients = Collections.emptyList(); // Пустой список ингредиентов
        Order order = new Order(ingredients);

        Response orderResponse = orderSteps.createOrder(null, order);
        System.out.println("Order Response: " + orderResponse.getBody().asString());

        Assert.assertEquals(400, orderResponse.getStatusCode()); // Ожидается ошибка
        Assert.assertTrue(orderResponse.getBody().asString().contains("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredients() {
        registerAndLoginUser();
        List<String> ingredients = Arrays.asList("invalid_hash1", "invalid_hash2"); // Неправильные хеши ингредиентов
        Order order = new Order(ingredients);

        Response orderResponse = orderSteps.createOrder(userToken, order);
        System.out.println("Order Response: " + orderResponse.getBody().asString());

        Assert.assertEquals(500, orderResponse.getStatusCode()); // Ожидается ошибка сервера
        Assert.assertTrue(orderResponse.getBody().asString().contains("Internal Server Error"));
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