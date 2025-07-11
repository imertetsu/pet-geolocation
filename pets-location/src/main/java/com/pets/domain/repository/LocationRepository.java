package com.pets.domain.repository;

import com.pets.domain.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {
    Optional<Location> findById(Long id);
    Optional<Location> findByPetId(Long petId);
    Location save(Location user);
    List<Location> findAll();
    void deleteById(Long locationId);
    Optional<Location> findLatestByPetId(Long petId);
    List<Location> findAllByPetId(Long petId);
}
