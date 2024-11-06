package com.lsm.service;

import com.lsm.events.*;
import com.lsm.exception.*;
import com.lsm.model.DTOs.AuthenticationResult;
import com.lsm.model.DTOs.LoginRequestDTO;
import com.lsm.model.DTOs.RegisterRequestDTO;
import com.lsm.model.DTOs.TokenRefreshResult;
import com.lsm.model.entity.RefreshToken;
import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.login.AccountLockedException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days
    private static final int MAX_REFRESH_TOKEN_PER_USER = 5;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_DURATION = 15; // minutes

    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;
    private final EventPublisher eventPublisher;

    @Transactional
    public AppUser registerUser(RegisterRequestDTO registerRequest) {
        validateRegistrationRequest(registerRequest);

        AppUser newUser = createUserFromRequest(registerRequest);
        AppUser savedUser = appUserRepository.save(newUser);

        eventPublisher.publishEvent(new UserRegisteredEvent(savedUser));
        log.info("User registered successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    @Transactional
    public AuthenticationResult authenticate(LoginRequestDTO loginRequest, String clientIp)
      throws AccountLockedException {
        if (loginAttemptService.isBlocked(clientIp)) {
            throw new AccountLockedException("Account is temporarily locked due to too many failed attempts");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            AppUser user = (AppUser) authentication.getPrincipal();

            if (!user.isEnabled()) {
                throw new AccountDisabledException("Account is disabled");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtTokenProvider.generateToken(user);
            RefreshToken refreshToken = createRefreshToken(user.getId());

            loginAttemptService.loginSucceeded(clientIp);
            eventPublisher.publishEvent(new UserLoginEvent(user));

            return new AuthenticationResult(accessToken, refreshToken.getToken(), user);

        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(clientIp);
            log.warn("Authentication failed for user: {}", loginRequest.getUsername());
            throw new AuthenticationException("Invalid username or password");
        }
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        cleanupOldRefreshTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(generateSecureToken())
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public TokenRefreshResult refreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(this::verifyRefreshToken)
                .map(token -> {
                    AppUser user = token.getUser();
                    String newAccessToken = jwtTokenProvider.generateToken(user);
                    return new TokenRefreshResult(newAccessToken, refreshToken);
                })
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
    }

    @Transactional
    public void logout(String token, String refreshToken) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            AppUser user = appUserRepository.findByUsername(username);
            if(user == null)
                throw new UserNotFoundException("User not found");

            // Invalidate specific refresh token if provided
            if (refreshToken != null) {
                refreshTokenRepository.deleteByToken(refreshToken);
            } else {
                // Invalidate all refresh tokens for the user
                refreshTokenRepository.deleteByUser(user);
            }

            jwtTokenProvider.invalidateToken(token);
            eventPublisher.publishEvent(new UserLogoutEvent(user));
            SecurityContextHolder.clearContext();

            log.info("User logged out successfully: {}", username);
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new LogoutException("Error during logout process");
        }
    }

    private void validateRegistrationRequest(RegisterRequestDTO request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        validatePassword(request.getPassword());
    }

    private void validatePassword(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()
        ));

        RuleResult result = validator.validate(new PasswordData(password));
        if (!result.isValid()) {
            throw new InvalidPasswordException("Password does not meet security requirements");
        }
    }

    private String generateSecureToken() {
        return UUID.randomUUID().toString() +
                UUID.randomUUID().toString();
    }

    private void cleanupOldRefreshTokens(AppUser user) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserOrderByExpiryDateDesc(user);
        if (tokens.size() >= MAX_REFRESH_TOKEN_PER_USER) {
            tokens.subList(MAX_REFRESH_TOKEN_PER_USER - 1, tokens.size())
                    .forEach(refreshTokenRepository::delete);
        }
    }

    private RefreshToken verifyRefreshToken(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token expired");
        }
        return token;
    }
}