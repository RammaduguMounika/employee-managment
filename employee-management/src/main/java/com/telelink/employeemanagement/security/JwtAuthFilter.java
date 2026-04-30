package com.telelink.employeemanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails
        .UserDetailsService;
import org.springframework.security.web.authentication
        .WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException, IOException {

        // Step 1 — Get Authorization header
        String authHeader = 
                request.getHeader("Authorization");
        log.info("Request URL: {}", request.getRequestURI());

        // Step 2 — Check if token exists
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            log.info("No token found — skipping!");
            filterChain.doFilter(request, response);
            return; // no token — skip filter!
        }

        // Step 3 — Extract token
        // remove "Bearer " prefix — get just the token!
        String token = authHeader.substring(7);
        log.info("Token found — validating!");

        // Step 4 — Extract username from token
        String username = jwtUtil.extractUsername(token);

        // Step 5 — Validate and set authentication
        if (username != null &&
                SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

            // load user from DB
            UserDetails userDetails =
                userDetailsService
                    .loadUserByUsername(username);

            // validate token
            if (jwtUtil.isTokenValid(token, userDetails)) {
                log.info("Token valid! User: {}", username);

                // tell Spring Security — this user is authenticated!
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            } else {
                log.warn("Token invalid!");
            }
        }

        // Step 6 — Continue to next filter
        filterChain.doFilter(request, response);
    }
}