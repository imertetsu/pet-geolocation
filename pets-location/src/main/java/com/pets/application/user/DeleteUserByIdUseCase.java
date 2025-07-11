package com.pets.application.user;

import com.pets.domain.repository.UserRepository;

import java.util.UUID;

public class DeleteUserByIdUseCase {

    private final UserRepository userRepository;

    public DeleteUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(UUID id) {
        userRepository.deleteById(id);
    }
}
