package com.telelink.employeemanagement.controller;

import com.telelink.employeemanagement.dto.AuthRequest;
import com.telelink.employeemanagement.dto.AuthResponse;
import com.telelink.employeemanagement.dto.RefreshRequest;
import com.telelink.employeemanagement.dto.RegisterRequest;
import com.telelink.employeemanagement.entity.RefreshToken;
import com.telelink.employeemanagement.entity.User;
import com.telelink.employeemanagement.repository.UserRepository;
import com.telelink.employeemanagement.security.JwtUtil;
import com.telelink.employeemanagement.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    // ── REGISTER ──────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Register request for: {}",
                request.getUsername());

        // check username not taken!
        if (userRepository.existsByUsername(
                request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(
                            null, null,
                            request.getUsername(),
                            "Username already exists!"));
        }

        // check email not taken!
        if (userRepository.existsByEmail(
                request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(
                            null, null,
                            request.getUsername(),
                            "Email already exists!"));
        }

        // create and save user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        log.info("User registered: {}",
                request.getUsername());

        // generate ACCESS token — 24 hours!
        String accessToken = jwtUtil.generateToken(
                request.getUsername());

        // generate REFRESH token — 7 days!
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        request.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                request.getUsername(),
                "User registered successfully!"));
    }

    // ── LOGIN ──────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request) {

        log.info("Login request for: {}",
                request.getUsername());

        // verify credentials!
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // generate ACCESS token — 24 hours!
        String accessToken = jwtUtil.generateToken(
                request.getUsername());

        // generate REFRESH token — 7 days!
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        request.getUsername());

        log.info("Login successful: {}",
                request.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                request.getUsername(),
                "Login successful!"));
    }

    // ── REFRESH TOKEN ──────────────────────────
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshRequest request) {

        log.info("Refresh token request received!");

        // Step 1 — verify refresh token!
        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken());

        // Step 2 — get username from refresh token!
        String username =
                refreshToken.getUser().getUsername();

        // Step 3 — generate NEW access token!
        String newAccessToken =
                jwtUtil.generateToken(username);

        log.info("New access token generated for: {}",
                username);

        // Step 4 — return new access token!
        // same refresh token — not changed!
        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                request.getRefreshToken(),
                username,
                "Token refreshed successfully!"));
    }

    // ── LOGOUT ─────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Valid @RequestBody RefreshRequest request) {

        log.info("Logout request received!");

        // delete refresh token from DB!
        refreshTokenService.deleteRefreshToken(
                request.getRefreshToken());

        log.info("User logged out successfully!");

        return ResponseEntity.ok(
                "Logged out successfully!");
    }
}