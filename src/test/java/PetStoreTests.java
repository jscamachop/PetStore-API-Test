import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class PetStoreTests {

    // Variable global para usarla en los tests
    String baseURI = "https://petstore.swagger.io/v2";

    // Datos de nuestro usuario de prueba
    String testUsername = "perfdog_user_123";
    String testPassword = "Password123!";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = this.baseURI;
    }

    @Test
    public void test01_createUser() {
        // 1. Preparamos el cuerpo (Body) de la petición en formato JSON
        String requestBody = "{\n" +
                "  \"id\": 0,\n" +
                "  \"username\": \"" + testUsername + "\",\n" +
                "  \"firstName\": \"Juan\",\n" +
                "  \"lastName\": \"Perez\",\n" +
                "  \"email\": \"juanperez@ejemplo.com\",\n" +
                "  \"password\": \"" + testPassword + "\",\n" +
                "  \"phone\": \"123456789\",\n" +
                "  \"userStatus\": 0\n" +
                "}";

        // 2. Ejecutamos la petición usando el patrón Given-When-Then de Rest Assured
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user")
                .then()
                .log().all() // Imprime la respuesta en consola para que podamos verla
                .statusCode(200) // Validamos que la respuesta sea OK
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", notNullValue()); // Validamos que nos devuelva un ID de mensaje
    }
}
