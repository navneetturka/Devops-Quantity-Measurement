package com.apps.quantitymeasurement.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateToken_ReturnsWellFormedJwt() {
        String token = jwtService.generateToken(
                "user@gmail.com", "John Doe", "http://pic.url/a.png");
        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    void testGenerateToken_NullEmail_ThrowsException() {
        assertThatThrownBy(() -> jwtService.generateToken(null, "John Doe", "pic"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGenerateToken_BlankEmail_ThrowsException() {
        assertThatThrownBy(() -> jwtService.generateToken("  ", "John Doe", "pic"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testExtractEmail_ReturnsCorrectSubject() {
        String token = jwtService.generateToken(
                "user@gmail.com", "John Doe", "http://pic.url");
        assertThat(jwtService.extractEmail(token)).isEqualTo("user@gmail.com");
    }

    @Test
    void testExtractClaim_NameClaimMatchesInput() {
        String token = jwtService.generateToken(
                "jane@gmail.com", "Jane Roe", "http://pic.url");
        String name = jwtService.extractClaim(
                token, claims -> claims.get("name", String.class));
        assertThat(name).isEqualTo("Jane Roe");
    }

    @Test
    void testIsTokenValid_ForFreshToken_ReturnsTrue() {
        String token = jwtService.generateToken(
                "user@gmail.com", "John Doe", "http://pic.url");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void testIsTokenValid_ForTamperedSignature_ReturnsFalse() {
        String token = jwtService.generateToken(
                "user@gmail.com", "John Doe", "http://pic.url");
        String tampered = token.substring(0, token.length() - 3) + "xyz";
        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }

    @Test
    void testIsTokenValid_ForMalformedToken_ReturnsFalse() {
        assertThat(jwtService.isTokenValid("not-a-jwt-at-all")).isFalse();
    }

    @Test
    void testIsTokenValid_ForEmptyToken_ReturnsFalse() {
        assertThat(jwtService.isTokenValid("")).isFalse();
    }
}