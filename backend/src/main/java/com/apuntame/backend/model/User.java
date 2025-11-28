package com.apuntame.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Users")
public class User implements UserDetails {

    @Id
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "takenBy", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> ordersTaken = new ArrayList<>();

    @OneToMany(mappedBy = "chargedBy", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> ordersCharged = new ArrayList<>();

    @OneToMany(mappedBy = "preparedBy", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> ordersPrepared = new ArrayList<>();

    @OneToMany(mappedBy = "deliveredBy", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> ordersDelivered = new ArrayList<>();

    public User() {
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Order> getOrdersTaken() {
        return ordersTaken;
    }

    public void setOrdersTaken(List<Order> ordersTaken) {
        this.ordersTaken = ordersTaken;
    }

    public List<Order> getOrdersCharged() {
        return ordersCharged;
    }

    public void setOrdersCharged(List<Order> ordersCharged) {
        this.ordersCharged = ordersCharged;
    }

    public List<Order> getOrdersPrepared() {
        return ordersPrepared;
    }

    public void setOrdersPrepared(List<Order> ordersPrepared) {
        this.ordersPrepared = ordersPrepared;
    }

    public List<Order> getOrdersDelivered() {
        return ordersDelivered;
    }

    public void setOrdersDelivered(List<Order> ordersDelivered) {
        this.ordersDelivered = ordersDelivered;
    }
}
