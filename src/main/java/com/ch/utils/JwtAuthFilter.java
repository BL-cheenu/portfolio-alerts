package com.ch.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * US3 - JWT Auth Filter
 * Validates JWT token from Authorization header for protected endpoints
 * Allows /api/v1/auth/** (login, register) without token
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ── Skip filter for public endpoints ────────────────────────────
        if (path.startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Extract Authorization header ─────────────────────────────────
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"msg\":\"Authorization token missing.\",\"status\":\"FAILED\",\"statusCode\":401}");
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        // ── Validate token ───────────────────────────────────────────────
        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Invalid or expired JWT token for path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"msg\":\"Invalid or expired token.\",\"status\":\"FAILED\",\"statusCode\":401}");
            return;
        }

        // ── Token valid — extract username and set in request ────────────
        String username = jwtUtil.extractUsername(token);
        request.setAttribute("username", username);
        log.debug("JWT valid for user: {}", username);

        filterChain.doFilter(request, response);
    }
}
