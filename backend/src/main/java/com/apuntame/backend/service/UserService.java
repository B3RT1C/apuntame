package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.constant.UserConstants;
import com.apuntame.backend.exception.DuplicateResourceException;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.model.User;
import com.apuntame.backend.repository.ActiveTokenRepository;
import com.apuntame.backend.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActiveTokenRepository activeTokenRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ActiveTokenRepository activeTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activeTokenRepository = activeTokenRepository;
    }

    public List<User> getAllUsers(Integer limit) {
        if (limit != null && limit > 0) {
            return userRepository.findAll(PageRequest.of(0, limit)).getContent();
        }
        return userRepository.findAll();
    }

    public User createUser(User user) {
        validateUser(user);

        if (userRepository.existsById(user.getUsername())) {
            throw new DuplicateResourceException(String.format(ErrorMessages.USER_ALREADY_EXISTS, user.getUsername()));
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole(UserConstants.DEFAULT_ROLE);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));
    }

    public User updateUser(String username, User userDetails) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));

        boolean shouldInvalidateToken = false;

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            shouldInvalidateToken = true;
        }

        if (userDetails.getRole() != null && !userDetails.getRole().isEmpty()) {
            user.setRole(userDetails.getRole());
            shouldInvalidateToken = true;
        }

        if (shouldInvalidateToken) {
            activeTokenRepository.deleteById(username);
        }

        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        if (!userRepository.existsById(username)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username));
        }
        activeTokenRepository.deleteById(username);
        userRepository.deleteById(username);
    }

    public List<Order> getOrdersTaken(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));
        return user.getOrdersTaken();
    }

    public List<Order> getOrdersCharged(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));
        return user.getOrdersCharged();
    }

    public List<Order> getOrdersPrepared(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));
        return user.getOrdersPrepared();
    }

    public List<Order> getOrdersDelivered(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));
        return user.getOrdersDelivered();
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessages.USERNAME_EMPTY);
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessages.PASSWORD_EMPTY);
        }
    }
}
