package com.telelink.employeemanagement.service;

import com.telelink.employeemanagement.entity.RefreshToken;
import com.telelink.employeemanagement.entity.User;
import com.telelink.employeemanagement.repository.RefreshTokenRepository;
import com.telelink.employeemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // 7 days expiry
    private static final int REFRESH_TOKEN_DAYS = 7;

    // Method 1 — Create refresh token
    @Transactional
    public RefreshToken createRefreshToken(String username) {

        log.info("Creating refresh token for: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + username));

        // delete old refresh token if exists!
        // MUST flush after delete before insert!
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush(); // ← ADD THIS LINE!

        // create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(REFRESH_TOKEN_DAYS));

        return refreshTokenRepository.save(refreshToken);
    }

    // Method 2 — Verify refresh token
    @Transactional
    public RefreshToken verifyRefreshToken(String token) {

        log.info("Verifying refresh token");

        // find token in DB
        RefreshToken refreshToken =
            refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Refresh token not found! Please login again!"));

        // check if token is expired!
        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            // delete expired token from DB!
            refreshTokenRepository.delete(refreshToken);

            log.warn("Refresh token expired for user: {}",
                refreshToken.getUser().getUsername());

            throw new RuntimeException(
                "Refresh token expired! Please login again!");
        }

        log.info("Refresh token valid for: {}",
            refreshToken.getUser().getUsername());

        return refreshToken; // valid!
    }

    // Method 3 — Delete refresh token (logout)
    @Transactional
    public void deleteRefreshToken(String token) {

        log.info("Deleting refresh token");

        RefreshToken refreshToken =
            refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Refresh token not found!"));

        refreshTokenRepository.delete(refreshToken);
        log.info("Refresh token deleted successfully!");
    }
}