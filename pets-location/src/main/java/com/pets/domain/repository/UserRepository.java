package com.pets.domain.repository;

import com.pets.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User save(User user);
    List<User> findAll();
    void deleteById(UUID userId);
}
