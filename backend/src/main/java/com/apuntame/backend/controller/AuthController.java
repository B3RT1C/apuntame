package com.apuntame.backend.controller;

import com.apuntame.backend.dto.LoginResponse;
import com.apuntame.backend.model.User;
import com.apuntame.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
        LoginResponse response = authService.login(user);
        return ResponseEntity.ok(response);
    }
}