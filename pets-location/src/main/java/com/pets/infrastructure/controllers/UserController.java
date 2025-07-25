package com.pets.infrastructure.controllers;

import com.pets.application.user.DeleteUserByIdUseCase;
import com.pets.application.user.GetAllUsersUseCase;
import com.pets.application.user.GetUserByIdUseCase;
import com.pets.application.user.RegisterUserUseCase;
import com.pets.domain.model.Pet;
import com.pets.domain.model.User;
import com.pets.domain.model.UserRole;
import com.pets.domain.records.UserRequest;
import com.pets.domain.records.UserResponse;
import com.pets.infrastructure.security.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final PasswordService passwordService;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final DeleteUserByIdUseCase deleteUserByIdUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    @Autowired
    public UserController(
            RegisterUserUseCase registerUserUseCase,
            PasswordService passwordService,
            GetAllUsersUseCase getAllUsersUseCase,
            DeleteUserByIdUseCase deleteUserByIdUseCase,
            GetUserByIdUseCase getUserByIdUseCase){
        this.registerUserUseCase = registerUserUseCase;
        this.passwordService = passwordService;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.deleteUserByIdUseCase = deleteUserByIdUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
    }
    @PostMapping()
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        String encodedPassword = passwordService.encodePassword(request.password());
        User user = registerUserUseCase.execute(
                request.name(),
                request.email(),
                encodedPassword,
                request.roles()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getIsVerified(), user.getPhotoUrl(), user.getProvider(), user.getRoles(), user.getPets()));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = getAllUsersUseCase.execute();
        List<UserResponse> response = users.stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getIsVerified(), u.getPhotoUrl(), u.getProvider(), u.getRoles(), u.getPets()))
                .toList();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        try {
            User user = getUserByIdUseCase.execute(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        deleteUserByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
