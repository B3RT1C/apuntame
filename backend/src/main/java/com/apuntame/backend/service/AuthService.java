package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.dto.LoginRequest;
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

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findById(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND, loginRequest.getUsername())
                ));

        Optional<ActiveToken> existingTokenOpt = activeTokenRepository.findById(user.getUsername());

        String token;
        if (existingTokenOpt.isPresent()) {
            ActiveToken existingToken = existingTokenOpt.get();

            if (!existingToken.isExpired()) {
                token = existingToken.getToken();
            } else {
                token = jwtUtil.generateToken(user.getUsername());
                existingToken.setToken(token);
                existingToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtExpiration / 1000));
                activeTokenRepository.save(existingToken);
            }
        } else {
            token = jwtUtil.generateToken(user.getUsername());
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtExpiration / 1000);
            ActiveToken activeToken = new ActiveToken(user.getUsername(), token, expiresAt);
            activeTokenRepository.save(activeToken);
        }

        return new LoginResponse(token, user.getUsername(), user.getRole());
    }
}