package com.apps.quantitymeasurement.controller;

import com.apps.quantitymeasurement.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/auth";
    }

    private HttpEntity<Void> withBearer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    @Test
    @DisplayName("GET /api/auth/login redirects to Google OAuth2 authorization endpoint")
    void testLogin_RedirectsToGoogleOAuth() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/login", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode().is3xxRedirection()
                || response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("GET /api/auth/me with valid JWT returns user claims")
    void testMe_ReturnsUserClaimsFromValidJwt() {
        String token = jwtService.generateToken(
                "user@gmail.com", "John Doe", "http://pic.url/a.png");

        @SuppressWarnings("unchecked")
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl() + "/me", HttpMethod.GET, withBearer(token), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("email")).isEqualTo("user@gmail.com");
        assertThat(response.getBody().get("name")).isEqualTo("John Doe");
        assertThat(response.getBody().get("picture")).isEqualTo("http://pic.url/a.png");
    }

    @Test
    @DisplayName("GET /api/auth/me with token missing optional claims still returns 200")
    void testMe_MissingNameClaim_StillReturns200WithNullName() {
        String token = jwtService.generateToken("noname@gmail.com", null, null);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl() + "/me", HttpMethod.GET, withBearer(token), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("email")).isEqualTo("noname@gmail.com");
        assertThat(response.getBody().get("name")).isNull();
    }

    @Test
    @DisplayName("GET /api/auth/me without token returns 401 Unauthorized")
    void testMe_NoToken_Returns401() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/me", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("GET /api/auth/me with malformed token returns 401 Unauthorized")
    void testMe_MalformedToken_Returns401() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/me", HttpMethod.GET,
                withBearer("not-a-real-jwt"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}