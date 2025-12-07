package com.apuntame.backend.controller;

import com.apuntame.backend.dto.UserDTO;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.model.User;
import com.apuntame.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) Integer limit) {
        List<User> users = userService.getAllUsers(limit);
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.fromUser(createdUser));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(UserDTO.fromUser(user));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(username, userDetails);
        return ResponseEntity.ok(UserDTO.fromUser(updatedUser));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/orders-taken")
    public ResponseEntity<List<Order>> getOrdersTaken(@PathVariable String username) {
        List<Order> orders = userService.getOrdersTaken(username);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{username}/orders-charged")
    public ResponseEntity<List<Order>> getOrdersCharged(@PathVariable String username) {
        List<Order> orders = userService.getOrdersCharged(username);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{username}/orders-prepared")
    public ResponseEntity<List<Order>> getOrdersPrepared(@PathVariable String username) {
        List<Order> orders = userService.getOrdersPrepared(username);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{username}/orders-delivered")
    public ResponseEntity<List<Order>> getOrdersDelivered(@PathVariable String username) {
        List<Order> orders = userService.getOrdersDelivered(username);
        return ResponseEntity.ok(orders);
    }
}