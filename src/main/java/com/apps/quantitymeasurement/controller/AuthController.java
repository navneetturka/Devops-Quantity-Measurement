package com.apps.quantitymeasurement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Google OAuth2 login and JWT session endpoints")
public class AuthController {

    private static final Logger logger =
            Logger.getLogger(AuthController.class.getName());

    @GetMapping("/login")
    @Operation(summary = "Redirect to Google OAuth2 login")
    public void login(HttpServletResponse response) throws IOException {
        logger.info("GET /api/auth/login - redirecting to Google");
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user's claims from the JWT")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        logger.info("GET /api/auth/me");
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("email", jwt.getSubject());
        user.put("name", jwt.getClaimAsString("name"));
        user.put("picture", jwt.getClaimAsString("picture"));
        user.put("issuedAt", jwt.getIssuedAt());
        user.put("expiresAt", jwt.getExpiresAt());
        return user;
    }
}