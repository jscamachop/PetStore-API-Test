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

    @Test
    public void test02_loginUser() {
        // 1. Creamos el cuerpo del usuario
        String requestBody = "{\n" +
                "  \"id\": 0,\n" +
                "  \"username\": \"" + testUsername + "\",\n" +
                "  \"password\": \"" + testPassword + "\"\n" +
                "}";

        // 2. Registramos al usuario en la API (igual que en el test 1, pero sin tantas validaciones)
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user")
                .then()
                .statusCode(200);


        // 3. Ejecutamos el Login
        given()
                .queryParam("username", testUsername)
                .queryParam("password", testPassword)
                .when()
                .get("/user/login")
                .then()
                .log().all() // Imprimimos la respuesta
                .statusCode(200) // Validamos que el login sea exitoso
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", notNullValue()); // El mensaje suele traer el token/sesión de inicio
    }

    @Test
    public void test03_findAvailablePets() {
        given()
                // Pasamos el parámetro ?status=available en la URL
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .log().all() // Imprimimos la respuesta en consola
                .statusCode(200) // Validamos que la petición sea exitosa
                // Validamos que el estado de la primera mascota en la lista sea realmente "available"
                .body("[0].status", equalTo("available"));
    }

    @Test
    public void test04_getPetById() {
        int petIdTest = 987654321;

        String petBody = "{\n" +
                "  \"id\": " + petIdTest + ",\n" +
                "  \"name\": \"Firulais_PerfDog\",\n" +
                "  \"status\": \"available\"\n" +
                "}";

        // Creamos la mascota temporalmente
        given()
                .contentType(ContentType.JSON)
                .body(petBody)
                .when()
                .post("/pet") // Endpoint para crear mascotas
                .then()
                .statusCode(200);

        given()
                // Usamos pathParam para inyectar el ID directamente en la URL
                .pathParam("petId", petIdTest)
                .when()
                .get("/pet/{petId}") // Rest Assured reemplazará {petId} con 987654321
                .then()
                .log().all() // Imprimimos la respuesta
                .statusCode(200) // Validamos que la encuentre
                .body("id", equalTo(petIdTest)) // Validamos que el ID coincida
                .body("name", equalTo("Firulais_PerfDog")); // Validamos que el nombre coincida
    }

    @Test
    public void test05_createOrder() {
        // 1. Preparamos los datos de nuestra orden de compra
        int orderIdTest = 55667788;
        int petIdTest = 987654321; // ID de la mascota que queremos "comprar"

        String orderBody = "{\n" +
                "  \"id\": " + orderIdTest + ",\n" +
                "  \"petId\": " + petIdTest + ",\n" +
                "  \"quantity\": 1,\n" +
                "  \"shipDate\": \"2026-03-13T19:00:00.000Z\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";

        // 2. Ejecutamos la petición POST para crear la orden
        given()
                .contentType(ContentType.JSON)
                .body(orderBody)
                .when()
                .post("/store/order")
                .then()
                .log().all() // Imprimimos la respuesta
                .statusCode(200) // Validamos que la orden se cree con éxito
                .body("id", equalTo(orderIdTest)) // Validamos que nos devuelva el ID correcto
                .body("status", equalTo("placed")); // Validamos el estado de la orden
    }

    @Test
    public void test06_logoutUser() {
        // 1. Iniciamos sesión rápidamente para tener algo que cerrar
        given()
                .queryParam("username", testUsername)
                .queryParam("password", testPassword)
                .when()
                .get("/user/login")
                .then()
                .statusCode(200);

        // 2. Consumimos el endpoint de logout
        given()
                .when()
                .get("/user/logout")
                .then()
                .log().all() // Imprimimos la respuesta en consola
                .statusCode(200) // Validamos que el código HTTP sea 200 (OK)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", equalTo("ok")); // La API confirma el logout con el mensaje "ok"
    }
}
