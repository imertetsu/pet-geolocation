package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.model.UserRole;
import com.pets.domain.repository.UserRoleRepository;
import com.pets.infrastructure.mapper.UserRoleMapper;
import com.pets.infrastructure.persistence.entities.UserEntity;
import com.pets.infrastructure.persistence.entities.UserRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleJpaRepository jpaRepository;

    @Override
    public List<UserRole> findByUser(UserEntity user) {
        return jpaRepository.findByUser(user)
                .stream()
                .map(UserRoleMapper::toDomain)
                .collect(Collectors.toList());
    }
    @Override
    public UserRole save(UserRole role, UserEntity user) {
        UserRoleEntity entity = UserRoleMapper.toEntity(role, user);
        return UserRoleMapper.toDomain(jpaRepository.save(entity));
    }
}
