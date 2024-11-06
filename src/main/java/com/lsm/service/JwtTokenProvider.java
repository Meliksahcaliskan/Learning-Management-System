package com.lsm.service;

import com.lsm.exception.InvalidTokenException;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token.expiration}")
    private long jwtExpirationInMs;

    @Value("${app.jwt.issuer}")
    private String tokenIssuer;

    private Key signingKey;
    private final Set<String> tokenBlacklist = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for a user.
     *
     * @param userDetails the user details
     * @return the generated token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof AppUser) {
            claims.put("role", ((AppUser) userDetails).getRole().name());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .setIssuer(tokenIssuer)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts username from token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates if a token is valid for a user.
     *
     * @param token the JWT token
     * @param userDetails the user details
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            if (isTokenBlacklisted(token)) {
                return false;
            }

            final String username = getUsernameFromToken(token);
            Claims claims = extractAllClaims(token);

            return username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(claims) &&
                    claims.getIssuer().equals(tokenIssuer);

        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Malformed token");
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new InvalidTokenException("Token expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new InvalidTokenException("Unsupported token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new InvalidTokenException("Empty claims");
        }
    }

    /**
     * Invalidates a token by adding it to the blacklist.
     *
     * @param token the token to invalidate
     */
    public void invalidateToken(String token) {
        tokenBlacklist.add(token);
        log.info("Token invalidated");
    }

    /**
     * Gets user details from a token.
     *
     * @param token the JWT token
     * @return the AppUser
     */
    public AppUser getUserFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return AppUser.builder()
                .username(claims.getSubject())
                .role(extractRole(claims))
                .build();
    }

    private <T> T extractClaim(String token, Claims.ClaimExtractor<T> extractor) {
        final Claims claims = extractAllClaims(token);
        return extractor.extractClaim(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .requireIssuer(tokenIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to extract claims from token", e);
            throw new InvalidTokenException("Invalid token");
        }
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    private Role extractRole(Claims claims) {
        try {
            String roleString = claims.get("role", String.class);
            if (roleString != null) {
                return Role.valueOf(roleString);
            }
            return Role.ROLE_STUDENT;
        } catch (IllegalArgumentException e) {
            log.error("Invalid role in token claims: {}", e.getMessage());
            return Role.ROLE_STUDENT;
        }
    }
}