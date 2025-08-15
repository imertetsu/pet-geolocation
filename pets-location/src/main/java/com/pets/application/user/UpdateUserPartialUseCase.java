package com.pets.application.user;

import com.pets.domain.model.User;
import com.pets.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateUserPartialUseCase {

    private final UserRepository userRepository;

    public UpdateUserPartialUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Edita solo los campos permitidos: nombre, contraseña y URL de foto.
     *
     * @param userId       ID del usuario a actualizar
     * @param newName      Nuevo nombre (puede ser null si no se quiere cambiar)
     * @param newPassword  Nueva contraseña (puede ser null si no se quiere cambiar)
     * @param newPhotoUrl  Nueva URL de foto (puede ser null si no se quiere cambiar)
     * @return Usuario actualizado
     */
    public User execute(UUID userId, String newName, String newPassword, String newPhotoUrl) {
        // Buscar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Solo actualizamos si se envían valores no nulos
        if (newName != null && !newName.isBlank()) {
            user.setName(newName);
        }
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword); // aquí podrías encriptar si usas BCrypt
        }
        if (newPhotoUrl != null && !newPhotoUrl.isBlank()) {
            user.setPhotoUrl(newPhotoUrl);
        }

        // Guardar y devolver
        return userRepository.save(user);
    }
}
