package com.telelink.employeemanagement.repository;

import com.telelink.employeemanagement.entity.RefreshToken;
import com.telelink.employeemanagement.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository
                extends JpaRepository<RefreshToken, Long> {

    // find refresh token by token value
    Optional<RefreshToken> findByToken(String token);

    // delete refresh token by user
    // used during logout!
    @Modifying
    @Transactional
    int deleteByUser(User user);
}