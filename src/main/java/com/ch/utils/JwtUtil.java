package com.ch.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * US2 Requirement: JWT generation after successful password verification.
 * - Token valid for 24 hours
 * - Signed with HMAC SHA-256
 */
@Component
public class JwtUtil {
    // ── Secret key (min 256-bit for HS256) ──────────────────────────────
    private static final String SECRET = "CH_INVEST_APP_SECRET_KEY_2024_SUPER_SECURE_256BIT";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ── Token expiry: 24 hours ───────────────────────────────────────────
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    // ── Generate JWT token ───────────────────────────────────────────────
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Extract username from token ──────────────────────────────────────
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ── Validate token ───────────────────────────────────────────────────
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Extract all claims ───────────────────────────────────────────────
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
