package com.pets.infrastructure.persistence.adapter;

import com.pets.infrastructure.persistence.entities.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetJpaRepository extends JpaRepository<PetEntity, Long> {
    Optional<PetEntity> findByName(String name);
    List<PetEntity> findAllByUserId(UUID userId);
}
