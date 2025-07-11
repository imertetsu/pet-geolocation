package com.pets.infrastructure.persistence.adapter;

import com.pets.infrastructure.persistence.entities.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationJpaRepository extends JpaRepository<LocationEntity, Long> {
    Optional<LocationEntity> findFirstByPet_IdOrderByTimestampDesc(Long petId);
    Optional<LocationEntity> findFirstByPetIdOrderByTimestampDesc(Long petId); // última ubicación
    List<LocationEntity> findAllByPetIdOrderByTimestampAsc(Long petId); // historial ordenado
}

