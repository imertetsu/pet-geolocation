package com.pets.application.user;

import com.pets.domain.model.User;
import com.pets.domain.repository.UserRepository;

import java.util.List;

public class GetAllUsersUseCase {
    private final UserRepository userRepository;

    public GetAllUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute() {
        return userRepository.findAll();
    }
}
