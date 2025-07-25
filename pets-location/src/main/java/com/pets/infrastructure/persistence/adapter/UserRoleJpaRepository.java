package com.pets.infrastructure.persistence.adapter;

import com.pets.infrastructure.persistence.entities.UserEntity;
import com.pets.infrastructure.persistence.entities.UserRoleEntity;
import com.pets.infrastructure.persistence.entities.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    List<UserRoleEntity> findByUser(UserEntity user);
}
