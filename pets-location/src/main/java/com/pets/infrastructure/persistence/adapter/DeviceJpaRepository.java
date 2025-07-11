package com.pets.infrastructure.persistence.adapter;

import com.pets.infrastructure.persistence.entities.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceJpaRepository extends JpaRepository<DeviceEntity, String> {
    Optional<DeviceEntity> findById(String id);
    boolean existsByPetId(Long petId);
    Optional<DeviceEntity> findByPetId(Long petId);
}

