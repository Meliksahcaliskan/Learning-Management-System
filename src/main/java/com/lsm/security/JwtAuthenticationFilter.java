package com.lsm.security;

import com.lsm.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;

import com.lsm.service.JwtTokenProvider;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_LENGTH = 7;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final HttpSessionSecurityContextRepository securityContextRepository;

    // @Value("${security.jwt.header.xsrf:X-XSRF-TOKEN}")
    // private String xsrfHeader;

    @Value("${security.jwt.ignore-paths:/api/v1/auth/**,/swagger-ui/**,/api-docs/**}")
    private String[] ignorePaths;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws InvalidTokenException {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");

            if (shouldNotFilter(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            /*
            if (!validateXsrfToken(request)) {
                handleSecurityException(request, response,
                        new InvalidTokenException("Invalid XSRF token"));
                return;
            }
            */

            processToken(token, request, response);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handleSecurityException(request, response, e);
        } finally {
            // Clear context after request is completed
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(ignorePaths)
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_LENGTH);
        }
        return null;
    }

    /* Won't work on Mobile find an alternative or don't use.
    private boolean validateXsrfToken(HttpServletRequest request) {
        // Skip XSRF validation for GET requests
        if (request.getMethod().equals(HttpMethod.GET.name())) {
            return true;
        }

        String xsrfToken = request.getHeader(xsrfHeader);
        String sessionXsrfToken = (String) request.getSession().getAttribute("XSRF-TOKEN");

        return StringUtils.hasText(xsrfToken) &&
                StringUtils.hasText(sessionXsrfToken) &&
                xsrfToken.equals(sessionXsrfToken);
    }
    */

    private void processToken(
            String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenProvider.validateToken(token, userDetails)) {
                    Authentication authentication = createAuthentication(userDetails, request);
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                    securityContextRepository.saveContext(context, request, response);

                    // Add security headers
                    addSecurityHeaders(response);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process JWT token", e);
            throw e;
        }
    }

    private Authentication createAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }

    private void handleSecurityException(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        log.error("Security exception occurred", exception);

        if (exception instanceof InvalidTokenException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else if (exception instanceof AccessDeniedException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        handlerExceptionResolver.resolveException(request, response, null, exception);
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        // response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}