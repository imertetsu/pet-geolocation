package com.pets.domain.repository;

import com.pets.domain.model.UserRole;
import com.pets.infrastructure.persistence.entities.UserEntity;

import java.util.List;

public interface UserRoleRepository {
    List<UserRole> findByUser(UserEntity user);
    UserRole save(UserRole role, UserEntity user);
}
