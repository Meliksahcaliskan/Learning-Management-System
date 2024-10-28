package com.lsm.service;

import java.util.Date;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

public class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting jwtSecret and jwtExpirationInMs using ReflectionTestUtils
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", Encoders.BASE64.encode(key.getEncoded()));
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 1000 * 60 * 60 * 10); // 10 hours

        // Mocking UserDetails
        when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    public void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testExtractUsername() {
        String token = jwtTokenProvider.generateToken(userDetails);
        String username = jwtTokenProvider.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    public void testTokenValidity() {
        String token = jwtTokenProvider.generateToken(userDetails);
        assertTrue(jwtTokenProvider.isTokenValid(token, userDetails));
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        // Set expiration time to 1 second for testing
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 1000);
        String token = jwtTokenProvider.generateToken(userDetails);
    
        // Wait 1.5 seconds to ensure token is expired
        Thread.sleep(1500);
       
        // Capture the exception to handle the warning
        ExpiredJwtException exception = assertThrows(ExpiredJwtException.class, () -> {
            jwtTokenProvider.isTokenValid(token, userDetails);
        });
    
        // Optionally, assert specific message or other details about the exception
        assertNotNull(exception.getMessage()); // or check specific exception details if needed
    }
    

    @Test
    public void testExtractClaim() {
        String token = jwtTokenProvider.generateToken(userDetails);
        Date expiration = jwtTokenProvider.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);
    }
}
