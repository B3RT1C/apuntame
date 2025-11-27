package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.dto.LoginResponse;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.ActiveToken;
import com.apuntame.backend.model.User;
import com.apuntame.backend.repository.ActiveTokenRepository;
import com.apuntame.backend.repository.UserRepository;
import com.apuntame.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ActiveTokenRepository activeTokenRepository;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       UserRepository userRepository,
                       ActiveTokenRepository activeTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.activeTokenRepository = activeTokenRepository;
    }

    public LoginResponse login(User loginUser) {
        authenticateUser(loginUser);
        User user = findUserByUsername(loginUser.getUsername());
        String token = getOrCreateToken(user.getUsername());

        return new LoginResponse(token, user.getUsername(), user.getRole());
    }

    private void authenticateUser(User loginUser) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
    }

    private User findUserByUsername(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND, username)
                ));
    }

    private String getOrCreateToken(String username) {
        Optional<ActiveToken> existingTokenOpt = activeTokenRepository.findById(username);

        if (existingTokenOpt.isPresent()) {
            return handleExistingToken(existingTokenOpt.get());
        } else {
            return createNewToken(username);
        }
    }

    private String handleExistingToken(ActiveToken existingToken) {
        if (!existingToken.isExpired()) {
            return existingToken.getToken();
        }
        return refreshToken(existingToken);
    }

    private String refreshToken(ActiveToken existingToken) {
        String newToken = jwtUtil.generateToken(existingToken.getUsername());
        existingToken.setToken(newToken);
        existingToken.setExpiresAt(calculateExpirationTime());
        activeTokenRepository.save(existingToken);
        return newToken;
    }

    private String createNewToken(String username) {
        String token = jwtUtil.generateToken(username);
        ActiveToken activeToken = new ActiveToken(username, token, calculateExpirationTime());
        activeTokenRepository.save(activeToken);
        return token;
    }

    private LocalDateTime calculateExpirationTime() {
        return LocalDateTime.now().plusSeconds(jwtExpiration / 1000);
    }
}