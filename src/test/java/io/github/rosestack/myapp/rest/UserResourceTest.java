package io.github.rosestack.myapp.rest;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.rosestack.myapp.config.BaseIT;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserResourceTest extends BaseIT {

    @Test
    void getAllUsers_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("totalElements", Matchers.equalTo(2))
                .body("content.get(0).id", Matchers.equalTo(1000));
    }

    @Test
    void getAllUsers_filtered() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/users?filter=user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("totalElements", Matchers.equalTo(1))
                .body("content.get(0).id", Matchers.equalTo(1000));
    }

    @Test
    void getUser_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/users/1000")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", Matchers.equalTo("user"));
    }

    @Test
    void getUser_notFound() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/users/1666")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createUser_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(readResource("/requests/userDTORequest.json"))
                .when()
                .post("/api/users")
                .then()
                .statusCode(HttpStatus.CREATED.value());
        assertEquals(3L, userRepository.selectCount(Wrappers.lambdaQuery()));
    }

    @Test
    void createUser_missingField() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(readResource("/requests/userDTORequest_missingField.json"))
                .when()
                .post("/api/users")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                .body("fieldErrors.get(0).property", Matchers.equalTo("name"))
                .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    void updateUser_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(readResource("/requests/userDTORequest.json"))
                .when()
                .put("/api/users/1000")
                .then()
                .statusCode(HttpStatus.OK.value());
        assertEquals("test", userRepository.selectById((1000)).getName());
        assertEquals(2, userRepository.selectCount(Wrappers.lambdaQuery()));
    }

    @Test
    void deleteUser_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/users/1000")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, userRepository.selectCount(Wrappers.lambdaQuery()));
    }

}