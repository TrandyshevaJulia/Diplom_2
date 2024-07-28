package edu.praktikum.diplom;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserApiTests {

    private UserSteps userSteps = new UserSteps();
    private User testUser;
    private String token;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
    }

    @Before
    public void createTestUser() {
        testUser = User.generateRandomUser();
        Response response = userSteps.createUser(testUser);
        response.then().statusCode(200)
                .body("success", equalTo(true));
        token = response.jsonPath().getString("accessToken");
    }

    @After
    public void deleteTestUser() {
        if (token != null) {
            Response deleteResponse = userSteps.deleteUser(token);
            deleteResponse.then().statusCode(202)
                    .body("success", equalTo(true));
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUser() {
        User uniqueUser = User.generateRandomUser();
        Response response = userSteps.createUser(uniqueUser);
        response.then().statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createAlreadyRegisteredUser() {
        Response response = userSteps.createUser(testUser);
        response.then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя с отсутствующим обязательным полем")
    public void createUserWithMissingField() {
        String incompleteUserPayload = String.format("{\"email\": \"%s\"}", testUser.getEmail());
        Response response = given()
                .header("Content-Type", "application/json")
                .body(incompleteUserPayload)
                .when()
                .post("/auth/register");
        response.then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
