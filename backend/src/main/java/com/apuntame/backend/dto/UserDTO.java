package com.apuntame.backend.dto;

import com.apuntame.backend.model.User;

public class UserDTO {
    private String username;
    private String role;

    public UserDTO() {}

    public UserDTO(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getUsername(), user.getRole());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
