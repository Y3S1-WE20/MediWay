package com.mediway.backend.security;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JWT Util Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "mySecretKey12345678901234567890123456789012345678901234567890";
    private final Long testExpiration = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Use reflection to set private fields
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        String username = "test@example.com";
        String role = "PATIENT";

        String token = jwtUtil.generateToken(username, role);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract username from token")
    void testExtractUsername() {
        String username = "user@example.com";
        String token = jwtUtil.generateToken(username, "DOCTOR");

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void testExtractExpiration() {
        String token = jwtUtil.generateToken("test@example.com", "ADMIN");

        Date expirationDate = jwtUtil.extractExpiration(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateToken() {
        String username = "valid@example.com";
        String token = jwtUtil.generateToken(username, "PATIENT");

        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate token with wrong username")
    void testInvalidateTokenWithWrongUsername() {
        String token = jwtUtil.generateToken("user1@example.com", "PATIENT");

        UserDetails userDetails = User.builder()
                .username("user2@example.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should extract role claim from token")
    void testExtractRoleClaim() {
        String username = "doctor@example.com";
        String role = "DOCTOR";
        String token = jwtUtil.generateToken(username, role);

        String extractedRole = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));

        assertEquals(role, extractedRole);
    }

    @Test
    @DisplayName("Should get expiration time")
    void testGetExpirationTime() {
        Long expirationTime = jwtUtil.getExpirationTime();

        assertNotNull(expirationTime);
        assertEquals(testExpiration, expirationTime);
    }

    @Test
    @DisplayName("Should handle expired token")
    void testExpiredToken() {
        // Create a token with very short expiration (1ms)
        JwtUtil shortExpirationJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", 1L); // 1 millisecond

        String token = shortExpirationJwtUtil.generateToken("user@example.com", "PATIENT");
        
        // Wait for token to expire
        try {
            Thread.sleep(100); // Wait 100ms to ensure token is expired
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        UserDetails userDetails = User.builder()
                .username("user@example.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // Token validation should throw ExpiredJwtException for expired tokens
        Exception exception = assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            shortExpirationJwtUtil.validateToken(token, userDetails);
        }, "Token should be expired and throw ExpiredJwtException");
        
        assertNotNull(exception, "Exception should be thrown for expired token");
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void testGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtUtil.generateToken("user1@example.com", "PATIENT");
        String token2 = jwtUtil.generateToken("user2@example.com", "DOCTOR");

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should generate different tokens for same user at different times")
    void testGenerateDifferentTokensAtDifferentTimes() throws InterruptedException {
        String token1 = jwtUtil.generateToken("user@example.com", "PATIENT");
        Thread.sleep(1100); // Wait more than 1 second to ensure different "iat" (issued at) claim
        String token2 = jwtUtil.generateToken("user@example.com", "PATIENT");

        assertNotEquals(token1, token2, "Tokens generated at different times should differ due to 'iat' claim");
    }
}
