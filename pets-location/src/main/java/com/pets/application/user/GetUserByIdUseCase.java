package com.pets.application.user;

import com.pets.domain.model.User;
import com.pets.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
