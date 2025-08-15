package com.pets.infrastructure.controllers;

import com.pets.application.user.*;
import com.pets.domain.model.User;
import com.pets.domain.records.EmailRequest;
import com.pets.domain.records.UpdateUserRequest;
import com.pets.domain.records.UserRequest;
import com.pets.domain.records.UserResponse;
import com.pets.infrastructure.notifications.EmailService;
import com.pets.infrastructure.notifications.VerificationService;
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
    private final RegisterUserLocalUseCase registerUserLocalUseCase;
    private final PasswordService passwordService;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final DeleteUserByIdUseCase deleteUserByIdUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final VerificationService verificationService;
    private final EmailService emailService;
    private final UpdateUserPartialUseCase updateUserPartialUseCase;

    @Autowired
    public UserController(
            RegisterUserLocalUseCase registerUserLocalUseCase,
            PasswordService passwordService,
            GetAllUsersUseCase getAllUsersUseCase,
            DeleteUserByIdUseCase deleteUserByIdUseCase,
            GetUserByIdUseCase getUserByIdUseCase,
            VerificationService verificationService,
            EmailService emailService,
            UpdateUserPartialUseCase updateUserPartialUseCase){
        this.registerUserLocalUseCase = registerUserLocalUseCase;
        this.passwordService = passwordService;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.deleteUserByIdUseCase = deleteUserByIdUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.verificationService = verificationService;
        this.emailService = emailService;
        this.updateUserPartialUseCase = updateUserPartialUseCase;
    }
    @PostMapping("/register/request-code")
    public ResponseEntity<Void> requestVerificationCode(@RequestBody EmailRequest request) {
        verificationService.generateAndSendCode(request.email(), emailService);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/register/verify")
    public ResponseEntity<UserResponse> verifyAndRegister(@RequestBody UserRequest request) {
        boolean verified = verificationService.verifyCode(request.email(), request.code());
        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String encodedPassword = passwordService.encodePassword(request.password());
        User user = registerUserLocalUseCase.execute(
                request.name(),
                request.email(),
                encodedPassword,
                request.roles()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getIsVerified(),
                        user.getPhotoUrl(),
                        user.getProvider(),
                        user.getRoles(),
                        user.getPets()
                ));
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
    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUserPartial(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequest request
    ) {
        // Solo encriptar si el password no es nulo ni vacío
        String encodedPassword = null;
        if (request.password() != null && !request.password().isBlank()) {
            encodedPassword = passwordService.encodePassword(request.password());
        }

        User updatedUser = updateUserPartialUseCase.execute(
                userId,
                request.name(),
                encodedPassword,  // puede ser null si no se envió password
                request.photoUrl()
        );

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        deleteUserByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
