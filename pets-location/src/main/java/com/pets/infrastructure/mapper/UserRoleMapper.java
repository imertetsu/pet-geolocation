package com.pets.infrastructure.mapper;

import com.pets.domain.model.UserRole;
import com.pets.infrastructure.persistence.entities.UserEntity;
import com.pets.infrastructure.persistence.entities.UserRoleEntity;

public class UserRoleMapper {

    public static UserRole toDomain(UserRoleEntity entity) {
        if (entity == null) return null;

        UserRole domain = new UserRole();
        domain.setRole(entity.getRole());
        domain.setGrantedDate(entity.getGrantedDate());
        return domain;
    }

    public static UserRoleEntity toEntity(UserRole domain, UserEntity userEntity) {
        if (domain == null) return null;

        UserRoleEntity entity = new UserRoleEntity();
        entity.setRole(domain.getRole());
        entity.setGrantedDate(domain.getGrantedDate());
        entity.setUser(userEntity);  // Asociamos el usuario aquí

        return entity;
    }
}

