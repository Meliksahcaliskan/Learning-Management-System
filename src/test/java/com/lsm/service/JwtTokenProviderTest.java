package com.lsm.service;

import com.lsm.exception.InvalidTokenException;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private AppUser testUser;
    private final String testSecret = Base64.getEncoder().encodeToString("testSecretKey123!@#$%^&*(12345678901234567890)".getBytes());
    private final String testIssuer = "test-issuer";
    private final long testAccessExpiration = 3600000; // 1 hour
    private final long testRefreshExpiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = AppUser.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(Role.ROLE_TEACHER)
                .build();

        // Setup JWT properties
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "tokenIssuer", testIssuer);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", testAccessExpiration);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", testRefreshExpiration);

        // Initialize signing key
        jwtTokenProvider.init();

        // Setup Redis mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate valid access token")
        void shouldGenerateValidAccessToken() {
            // Act
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // Assert
            assertNotNull(token);
            assertEquals(testUser.getUsername(), jwtTokenProvider.getUsernameFromToken(token));
            assertEquals(testUser.getId(), jwtTokenProvider.getUserIdFromToken(token));
            assertEquals(testUser.getRole(), jwtTokenProvider.getRoleFromToken(token));
            assertEquals(JwtTokenProvider.TokenType.ACCESS, jwtTokenProvider.getTokenType(token));
        }

        @Test
        @DisplayName("Should generate valid refresh token")
        void shouldGenerateValidRefreshToken() {
            // Act
            String token = jwtTokenProvider.generateRefreshToken(testUser);

            // Assert
            assertNotNull(token);
            assertEquals(testUser.getUsername(), jwtTokenProvider.getUsernameFromToken(token));
            assertEquals(JwtTokenProvider.TokenType.REFRESH, jwtTokenProvider.getTokenType(token));
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate token successfully")
        void shouldValidateTokenSuccessfully() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(testUser);
            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            // Act & Assert
            assertTrue(jwtTokenProvider.validateToken(token, testUser));
        }

        @Test
        @DisplayName("Should reject blacklisted token")
        void shouldRejectBlacklistedToken() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(testUser);
            when(redisTemplate.hasKey(anyString())).thenReturn(true);

            // Act & Assert
            assertFalse(jwtTokenProvider.validateToken(token, testUser));
        }

        @Test
        @DisplayName("Should reject token with invalid signature")
        void shouldRejectInvalidSignature() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(testUser) + "invalid";

            // Act & Assert
            assertThrows(InvalidTokenException.class,
                    () -> jwtTokenProvider.validateToken(token, testUser));
        }

        @Test
        @DisplayName("Should reject token with wrong user")
        void shouldRejectWrongUser() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(testUser);
            AppUser wrongUser = AppUser.builder()
                    .username("wronguser")
                    .build();

            // Act & Assert
            assertFalse(jwtTokenProvider.validateToken(token, wrongUser));
        }
    }

    @Nested
    @DisplayName("Token Invalidation Tests")
    class TokenInvalidationTests {

        @Test
        @DisplayName("Should invalidate token successfully")
        void shouldInvalidateTokenSuccessfully() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // Act
            jwtTokenProvider.invalidateToken(token);

            // Assert
            verify(valueOperations).set(
                    argThat(key -> key.startsWith("token:blacklist:")),
                    eq("blacklisted"),
                    anyLong(),
                    eq(TimeUnit.MILLISECONDS)
            );
        }

        @Test
        @DisplayName("Should handle invalid token during invalidation")
        void shouldHandleInvalidTokenDuringInvalidation() {
            // Arrange
            String invalidToken = "invalid.token.string";

            // Act & Assert
            assertThrows(InvalidTokenException.class,
                    () -> jwtTokenProvider.invalidateToken(invalidToken));
        }
    }

    @Nested
    @DisplayName("User Extraction Tests")
    class UserExtractionTests {

        @Test
        @DisplayName("Should extract user details from token")
        void shouldExtractUserDetailsFromToken() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // Act
            AppUser extractedUser = jwtTokenProvider.getUserFromToken(token);

            // Assert
            assertNotNull(extractedUser);
            assertEquals(testUser.getId(), extractedUser.getId());
            assertEquals(testUser.getUsername(), extractedUser.getUsername());
            assertEquals(testUser.getEmail(), extractedUser.getEmail());
            assertEquals(testUser.getRole(), extractedUser.getRole());
        }

        @Test
        @DisplayName("Should handle invalid token during user extraction")
        void shouldHandleInvalidTokenDuringExtraction() {
            // Arrange
            String invalidToken = "invalid.token.string";

            // Act & Assert
            assertThrows(InvalidTokenException.class,
                    () -> jwtTokenProvider.getUserFromToken(invalidToken));
        }
    }

    @Nested
    @DisplayName("Token Claim Tests")
    class TokenClaimTests {

        @Test
        @DisplayName("Should extract all claims correctly")
        void shouldExtractAllClaimsCorrectly() {
            // Arrange
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // Act & Assert
            assertEquals(testUser.getUsername(), jwtTokenProvider.getUsernameFromToken(token));
            assertEquals(testUser.getId(), jwtTokenProvider.getUserIdFromToken(token));
            assertEquals(testUser.getRole(), jwtTokenProvider.getRoleFromToken(token));
            assertEquals(JwtTokenProvider.TokenType.ACCESS, jwtTokenProvider.getTokenType(token));
        }

        @Test
        @DisplayName("Should handle malformed token during claim extraction")
        void shouldHandleMalformedToken() {
            // Arrange
            String malformedToken = "malformed.token";

            // Act & Assert
            assertThrows(InvalidTokenException.class,
                    () -> jwtTokenProvider.getUsernameFromToken(malformedToken));
        }
    }
}