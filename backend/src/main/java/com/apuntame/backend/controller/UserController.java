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
                .map(user -> new UserDTO(user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        UserDTO userDTO = new UserDTO(createdUser.getUsername(), createdUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        UserDTO userDTO = new UserDTO(user.getUsername(), user.getRole());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(username, userDetails);
        UserDTO userDTO = new UserDTO(updatedUser.getUsername(), updatedUser.getRole());
        return ResponseEntity.ok(userDTO);
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