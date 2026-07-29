package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.model.QuantityInputDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementDTO;
import com.apps.quantitymeasurement.security.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuantityMeasurementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    private String jwtToken;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/quantities";
    }

    @BeforeEach
    void generateToken() {
        jwtToken = jwtService.generateToken(
                "test.user@gmail.com", "Test User", "http://pic.url");
    }

    private QuantityInputDTO input(
            double tv, String tu, String tm,
            double dv, String du, String dm) {
        QuantityInputDTO dto = new QuantityInputDTO();
        dto.setThisQuantityDTO(new QuantityDTO(tv, tu, tm));
        dto.setThatQuantityDTO(new QuantityDTO(dv, du, dm));
        return dto;
    }

    private QuantityInputDTO inputWithTarget(
            double tv, String tu, String tm,
            double dv, String du, String dm,
            double rv, String ru, String rm) {
        QuantityInputDTO dto = input(tv, tu, tm, dv, du, dm);
        dto.setTargetQuantityDTO(new QuantityDTO(rv, ru, rm));
        return dto;
    }

    private HttpEntity<QuantityInputDTO> json(QuantityInputDTO body) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(jwtToken);
        return new HttpEntity<>(body, h);
    }

    private HttpEntity<Void> authOnly() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwtToken);
        return new HttpEntity<>(h);
    }

    @Test @Order(1)
    @DisplayName("Application context loads and test server starts")
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
        assertThat(port).isGreaterThan(0);
    }

    @Test @Order(2)
    @DisplayName("POST /compare - 1 foot equals 12 inches → true")
    void testCompare_FootEqualsInches() {
        var r = restTemplate.exchange(
                baseUrl() + "/compare", HttpMethod.POST,
                json(input(1.0,"FEET","LengthUnit",
                        12.0,"INCHES","LengthUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody().getResultString()).isEqualTo("true");
    }

    @Test @Order(3)
    @DisplayName("POST /compare - 1 foot does NOT equal 1 inch → false")
    void testCompare_FootNotEqualInch() {
        var r = restTemplate.exchange(
                baseUrl() + "/compare", HttpMethod.POST,
                json(input(1.0,"FEET","LengthUnit",
                        1.0,"INCHES","LengthUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody().getResultString()).isEqualTo("false");
    }

    @Test @Order(4)
    @DisplayName("POST /compare - 1 gallon equals 3.78541 litres → true")
    void testCompare_GallonEqualsLitres() {
        var r = restTemplate.exchange(
                baseUrl() + "/compare", HttpMethod.POST,
                json(input(1.0,"GALLON","VolumeUnit",
                        3.78541,"LITRE","VolumeUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody().getResultString()).isEqualTo("true");
    }

    @Test @Order(5)
    @DisplayName("POST /compare - 212 Fahrenheit equals 100 Celsius → true")
    void testCompare_FahrenheitEqualsCelsius() {
        var r = restTemplate.exchange(
                baseUrl() + "/compare", HttpMethod.POST,
                json(input(212.0,"FAHRENHEIT","TemperatureUnit",
                        100.0,"CELSIUS","TemperatureUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody().getResultString()).isEqualTo("true");
    }

    @Test @Order(6)
    @DisplayName("POST /convert - 100 Celsius to Fahrenheit = 212")
    void testConvert_CelsiusToFahrenheit() {
        var r = restTemplate.exchange(
                baseUrl() + "/convert", HttpMethod.POST,
                json(input(100.0,"CELSIUS","TemperatureUnit",
                        0.0,"FAHRENHEIT","TemperatureUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Double) r.getBody().getResultValue()).isEqualTo(212.0);
    }

    @Test @Order(7)
    @DisplayName("POST /add - add 1 gallon and 3.785 litres ≈ 2 gallons")
    void testAdd_GallonAndLitres() {
        QuantityInputDTO body = input(
                1.0,   "GALLON", "VolumeUnit",
                3.785, "LITRE",  "VolumeUnit"
        );
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.exchange(
                baseUrl() + "/add", HttpMethod.POST,
                json(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultValue())
                .isCloseTo(2.0, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test @Order(8)
    @DisplayName("POST /add-with-target-unit - 1 foot + 12 inches target INCHES = 24")
    void testAddWithTargetUnit_FootAndInchesToInches() {
        var r = restTemplate.exchange(
                baseUrl() + "/add-with-target-unit", HttpMethod.POST,
                json(inputWithTarget(
                        1.0,"FEET","LengthUnit",
                        12.0,"INCHES","LengthUnit",
                        0.0,"INCHES","LengthUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Double) r.getBody().getResultValue()).isEqualTo(24.0);
    }

    @Test @Order(9)
    @DisplayName("POST /subtract - 2 feet minus 12 inches = 1 foot")
    void testSubtract_FeetMinusInches() {
        var r = restTemplate.exchange(
                baseUrl() + "/subtract", HttpMethod.POST,
                json(input(2.0,"FEET","LengthUnit",
                        12.0,"INCHES","LengthUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Double) r.getBody().getResultValue()).isEqualTo(1.0);
    }

    @Test @Order(10)
    @DisplayName("POST /subtract-with-target-unit - 2 feet minus 12 inches in INCHES = 12")
    void testSubtractWithTargetUnit() {
        var r = restTemplate.exchange(
                baseUrl() + "/subtract-with-target-unit", HttpMethod.POST,
                json(inputWithTarget(
                        2.0,"FEET","LengthUnit",
                        12.0,"INCHES","LengthUnit",
                        0.0,"INCHES","LengthUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Double) r.getBody().getResultValue()).isEqualTo(12.0);
    }

    @Test @Order(11)
    @DisplayName("POST /divide - 1 yard / 1 foot = 3.0")
    void testDivide_YardByFoot() {
        var r = restTemplate.exchange(
                baseUrl() + "/divide", HttpMethod.POST,
                json(input(1.0,"YARDS","LengthUnit",
                        1.0,"FEET","LengthUnit")),
                QuantityMeasurementDTO.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Double) r.getBody().getResultValue()).isEqualTo(3.0);
    }

    @Test @Order(12)
    @DisplayName("GET /history/operation/CONVERT - returns list not empty")
    @SuppressWarnings("unchecked")
    void testGetHistoryByOperation_Convert() {
        var r = restTemplate.exchange(
                baseUrl() + "/history/operation/CONVERT", HttpMethod.GET,
                authOnly(), List.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody()).isNotNull().isNotEmpty();
    }

    @Test @Order(13)
    @DisplayName("GET /history/type/TemperatureUnit - returns history not empty")
    @SuppressWarnings("unchecked")
    void testGetHistoryByType_Temperature() {
        var r = restTemplate.exchange(
                baseUrl() + "/history/type/TemperatureUnit", HttpMethod.GET,
                authOnly(), List.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody()).isNotNull().isNotEmpty();
    }

    @Test @Order(14)
    @DisplayName("GET /count/DIVIDE - returns count > 0")
    void testGetOperationCount_Divide() {
        var r = restTemplate.exchange(
                baseUrl() + "/count/DIVIDE", HttpMethod.GET,
                authOnly(), Long.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody()).isGreaterThan(0L);
    }

    @Test @Order(15)
    @DisplayName("POST /divide by zero → error, then /history/errored returns it")
    @SuppressWarnings("unchecked")
    void testDivide_ByZero_ErrorRecorded() {
        QuantityInputDTO body = input(
                1.0, "YARDS", "LengthUnit",
                0.0, "FEET",  "LengthUnit"
        );
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/divide", HttpMethod.POST,
                json(body), String.class);

        assertThat(response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError()).isTrue();
        assertThat(response.getBody()).containsIgnoringCase("zero");

        ResponseEntity<List> errorHistoryResponse = restTemplate.exchange(
                baseUrl() + "/history/errored", HttpMethod.GET,
                authOnly(), List.class);
        assertThat(errorHistoryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(16)
    @DisplayName("POST /compare - invalid unit 'FOOT' → 400 Bad Request")
    void testCompare_InvalidUnit_Returns400() {
        var r = restTemplate.exchange(
                baseUrl() + "/compare", HttpMethod.POST,
                json(input(1.0,"FOOT","LengthUnit",
                        12.0,"INCHES","LengthUnit")),
                String.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody())
                .contains("Unit must be valid for the specified measurement type");
    }

    @Test @Order(17)
    @DisplayName("POST /compare - invalid measurementType 'Length' → 400 Bad Request")
    void testCompare_InvalidType_Returns400() {
        var r = restTemplate.exchange(
                baseUrl() + "/compare", HttpMethod.POST,
                json(input(1.0,"FEET","Length",
                        12.0,"INCHES","LengthUnit")),
                String.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody()).contains(
                "Measurement type must be one of: LengthUnit, VolumeUnit, " +
                        "WeightUnit, TemperatureUnit");
    }

    @Test @Order(18)
    @DisplayName("UC18: POST /compare WITHOUT token → 401 Unauthorized")
    void testCompare_NoToken_Returns401() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QuantityInputDTO> entity = new HttpEntity<>(
                input(1.0,"FEET","LengthUnit",12.0,"INCHES","LengthUnit"), h);

        var r = restTemplate.exchange(
                baseUrl() + "/compare", HttpMethod.POST, entity, String.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test @Order(19)
    @DisplayName("UC18: GET history WITH invalid/tampered token → 401 Unauthorized")
    void testHistory_InvalidToken_Returns401() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("this.is.not-a-valid-jwt");
        HttpEntity<Void> entity = new HttpEntity<>(h);

        var r = restTemplate.exchange(
                baseUrl() + "/history/operation/COMPARE", HttpMethod.GET,
                entity, String.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test @Order(20)
    @DisplayName("UC18: GET /actuator/health is public → 200 without token")
    void testActuatorHealth_PublicEndpoint_NoTokenNeeded() {
        var r = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health", String.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(21)
    @DisplayName("UC18: GET /api/auth/login is public and redirects to Google")
    void testAuthLogin_PublicEndpoint_Redirects() {
        ResponseEntity<String> r = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/auth/login", String.class);
        assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }
}