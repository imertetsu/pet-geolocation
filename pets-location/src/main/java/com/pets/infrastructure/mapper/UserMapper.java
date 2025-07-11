package com.pets.infrastructure.mapper;

import com.pets.domain.model.User;
import com.pets.infrastructure.persistence.entities.PetEntity;
import com.pets.infrastructure.persistence.entities.UserEntity;
import com.pets.infrastructure.persistence.entities.UserRoleEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setIsVerified(entity.getIsVerified());

        // Convertir roles si existen
        if (entity.getRoles() != null) {
            user.setRoles(entity.getRoles().stream()
                    .map(UserRoleMapper::toDomain)
                    .collect(Collectors.toList()));
        }

        // Convertir mascotas
        if (entity.getPets() != null) {
            user.setPets(entity.getPets().stream()
                    .map(PetMapper::toDomain)
                    .collect(Collectors.toList()));
        }

        return user;
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setIsVerified(domain.getIsVerified());

        if (domain.getRoles() != null) {
            List<UserRoleEntity> roles = domain.getRoles().stream()
                    .map(UserRoleMapper::toEntity)
                    .toList();
            entity.setRoles(roles); // ✅ esto es CRUCIAL
        }

        // pets también si aplican
        if (domain.getPets() != null) {
            List<PetEntity> pets = domain.getPets().stream()
                    .map(PetMapper::toEntity)
                    .toList();
            entity.setPets(pets);
        }

        return entity;
    }
}

