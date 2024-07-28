package edu.praktikum.diplom;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserSteps {

    private static final String REGISTER_ENDPOINT = "/auth/register";
    private static final String LOGIN_ENDPOINT = "/auth/login";
    private static final String USER_ENDPOINT = "/auth/user";

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .header("Content-Type", "application/json")
                .body(user.toJson())
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        String loginPayload = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
        return given()
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post(LOGIN_ENDPOINT);
    }

    @Step("Удаление пользователя по токену")
    public Response deleteUser(String token) {
        return given()
                .header("Authorization", token)
                .when()
                .delete(USER_ENDPOINT);
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(String token, User user) {
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", token != null ? token : "")
                .body(user.toJson())
                .when()
                .patch(USER_ENDPOINT);
    }
}
