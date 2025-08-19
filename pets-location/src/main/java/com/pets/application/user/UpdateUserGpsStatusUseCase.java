package com.pets.application.user;

import com.pets.domain.model.User;
import com.pets.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateUserGpsStatusUseCase {

    private final UserRepository userRepository;

    public UpdateUserGpsStatusUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Habilita o deshabilita la opción de GPS para un usuario.
     *
     * @param userId      ID del usuario a actualizar
     * @param hasGpsDevice Valor a establecer (true = habilitado, false = deshabilitado)
     * @return Usuario actualizado
     */
    public User execute(UUID userId, Boolean hasGpsDevice) {
        // Buscar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Actualizar el campo
        user.setHasGpsDevice(hasGpsDevice);

        // Guardar y devolver
        return userRepository.save(user);
    }
}
