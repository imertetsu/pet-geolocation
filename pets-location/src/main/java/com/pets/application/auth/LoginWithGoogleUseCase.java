package com.pets.application.auth;

import com.pets.domain.model.User;
import com.pets.domain.model.UserRole;
import com.pets.domain.repository.UserRepository;
import com.pets.domain.repository.UserRoleRepository;
import com.pets.infrastructure.persistence.entities.AuthProvider;
import com.pets.infrastructure.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class LoginWithGoogleUseCase {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public User execute(String email, String name, String photoUrl, String googleId) {
        return userRepository.findByEmail(email).map(user -> {
            if (user.getProvider() != AuthProvider.GOOGLE) {
                throw new RuntimeException("Este correo ya está registrado con otro método de acceso: " + user.getProvider());
            }

            // Asegurar que tenga CUSTOMER si no lo tiene
            assignRoleIfNotExists(user, "CUSTOMER");
            return user;
        }).orElseGet(() -> {
            // Crear nuevo usuario
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPhotoUrl(photoUrl);
            newUser.setIsVerified(true);
            newUser.setProvider(AuthProvider.GOOGLE);
            newUser.setProviderId(googleId);

            // Guardar usuario primero para generar ID
            newUser = userRepository.save(newUser);

            // Asignar rol CUSTOMER
            assignRoleIfNotExists(newUser, "CUSTOMER");

            return newUser;
        });
    }

    private void assignRoleIfNotExists(User user, String roleName) {
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

        boolean exists = user.getRoles()
                .stream()
                .anyMatch(r -> r.getRole().equals(roleName));

        if (!exists) {
            UserRole role = new UserRole(roleName, LocalDateTime.now());
            userRoleRepository.save(role, toEntity(user)); // Guardamos con relación
            user.getRoles().add(role);
        }
    }

    private UserEntity toEntity(User user) {
        UserEntity e = new UserEntity();
        e.setId(user.getId());
        return e;
    }
}
