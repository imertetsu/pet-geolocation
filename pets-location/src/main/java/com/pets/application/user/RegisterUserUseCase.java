package com.pets.application.user;

import com.pets.domain.model.User;
import com.pets.domain.model.UserRole;
import com.pets.domain.repository.UserRepository;
import com.pets.infrastructure.persistence.entities.AuthProvider;
import com.pets.infrastructure.persistence.entities.UserRoleEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String name, String email, String password, List<String> roleNames) {
        // Validar si ya existe
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("El email ya está en uso.");
        });

        // Crear usuario básico (sin roles aún)
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setIsVerified(true);
        user.setProvider(AuthProvider.GOOGLE);

        // Guardar para generar el ID
        user = userRepository.save(user);
        UUID userId = user.getId();

        // Crear los roles como objetos del dominio
        List<UserRole> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            UserRole role = new UserRole();
            role.setRole(roleName);
            role.setGrantedDate(LocalDateTime.now());
            roles.add(role);
        }

        // Asociar roles y volver a guardar
        user.setRoles(roles);
        return userRepository.save(user);
    }
}