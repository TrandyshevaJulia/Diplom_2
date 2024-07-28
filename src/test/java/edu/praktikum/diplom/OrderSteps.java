package edu.praktikum.diplom;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    private static final String ORDERS_ENDPOINT = "/orders";

    @Step("Создание заказа")
    public Response createOrder(String token, Order order) {
        String orderJson = order.toJson();
        System.out.println("Order JSON: " + orderJson); // Добавлено для отладки

        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", token != null ? token : "")
                .body(orderJson)
                .when()
                .post(ORDERS_ENDPOINT);
    }
    @Step("Получение заказов конкретного пользователя")
    public Response getOrders(String token) {
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", token != null ? token : "")
                .when()
                .get(ORDERS_ENDPOINT);
    }
}