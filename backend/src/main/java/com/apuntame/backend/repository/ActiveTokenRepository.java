package com.apuntame.backend.repository;

import com.apuntame.backend.model.ActiveToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveTokenRepository extends JpaRepository<ActiveToken, String> {
    Optional<ActiveToken> findByToken(String token);
}