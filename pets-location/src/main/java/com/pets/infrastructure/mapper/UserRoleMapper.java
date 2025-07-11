package com.pets.infrastructure.mapper;

import com.pets.domain.model.UserRole;
import com.pets.infrastructure.persistence.entities.UserRoleEntity;

public class UserRoleMapper {

    public static UserRole toDomain(UserRoleEntity entity) {
        if (entity == null) return null;

        UserRole domain = new UserRole();
        domain.setUserId(entity.getUserId());
        domain.setRole(entity.getRole());
        domain.setGrantedDate(entity.getGrantedDate());
        return domain;
    }

    public static UserRoleEntity toEntity(UserRole domain) {
        if (domain == null) return null;

        UserRoleEntity entity = new UserRoleEntity();
        entity.setUserId(domain.getUserId());
        entity.setRole(domain.getRole());
        entity.setGrantedDate(domain.getGrantedDate());

        return entity;
    }
}

