package com.telelink.employeemanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key — used to sign token
    private static final String SECRET_KEY =
        "telelinksecretkeytelelinksecretkey123";

    // Token valid for 24 hours
    private static final long EXPIRATION =
        1000 * 60 * 60 * 24;

    // Step 1 — Generate token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)        // who this token belongs to
                .setIssuedAt(new Date())     // when created
                .setExpiration(new Date(     // when expires
                    System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(),   // sign with secret key
                    SignatureAlgorithm.HS256)
                .compact();                  // build token string
    }

    // Step 2 — Extract username from token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // returns username
    }

    // Step 3 — Validate token
    public boolean isTokenValid(String token,
                                UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(
                    userDetails.getUsername()) // username matches?
                && !isTokenExpired(token);     // not expired?
    }

    // Check if token expired
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date()); // expiry before now?
    }

    // Convert secret string to Key object
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes());
    }
}