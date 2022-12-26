import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @BeforeEach
    public void initialize() {
        RestAssured.baseURI = "https://petstore.swagger.io/";
    }

    @Test
    public void managingPayload() {
        var fileHelper = new FileHelper();

        var payload = fileHelper.getFile("pet.json");

        var jsonHelper = new JSONHelper();

        payload = jsonHelper.updateJsonValue(payload, "id", "10");
        payload = jsonHelper.updateJsonValue(payload, "category.id", "1");
        payload = jsonHelper.updateJsonValue(payload, "photoUrls[0]", "shorturl.at/rJXZ2");
        payload = jsonHelper.updateJsonValue(payload, "tags[0].id", "1");
        payload = jsonHelper.updateJsonValue(payload, "tags[0].name", "#dog");
        payload = jsonHelper.updateJsonValue(payload, "status", "sold");

        given()
                .basePath("{version}/pet")
                .pathParam("version", "v2")
                .filter(new RestReqFilter())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("category.id", equalTo(1))
                .body("photoUrls[0]", equalTo("shorturl.at/rJXZ2"))
                .body("tags[0].id", equalTo(1))
                .body("tags[0].name", equalTo("#dog"))
                .body("status", equalTo("sold"));
    }

    @Test
    public void getResponseAsMap() {
        var fileHelper = new FileHelper();

        var payload = fileHelper.getFile("pet.json");

        var jsonHelper = new JSONHelper();

        payload = jsonHelper.updateJsonValue(payload, "id", "1");
        payload = jsonHelper.updateJsonValue(payload, "category.id", "2");
        payload = jsonHelper.updateJsonValue(payload, "photoUrls[0]", "shorturl.at/rJXZe4");
        payload = jsonHelper.updateJsonValue(payload, "tags[0].id", "2");
        payload = jsonHelper.updateJsonValue(payload, "tags[0].name", "#cat");
        payload = jsonHelper.updateJsonValue(payload, "status", "sold");

        var response = given()
                .basePath("{version}/pet")
                .pathParam("version", "v2")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post();
        var responseMap = response.as(new TypeRef<Map<String, Object>>() {
        });
        assertEquals(responseMap.get("id"), 1);
        assertEquals(((Map<String, Object>) responseMap.get("category")).get("id"), 2);

        var tagId = response.jsonPath().getInt("tags[0].id");
        assertEquals(tagId, 2);

    }

    @Test
    public void cURLExample() {
        var fileHelper = new FileHelper();
        int desiredStatusCode = 200;
        var payload = fileHelper.getFile("pet.json");

        given()
                .basePath("{version}/pet")
                .pathParam("version", "v2")
                .filter(new RestReqFilter(desiredStatusCode))
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post()
                .then()
                .statusCode(desiredStatusCode);
    }
}
