package com.pets.domain.repository;

import com.pets.domain.model.Pet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PetRepository {
    Optional<Pet> findById(Long id);
    Optional<Pet> findByName(String name);

    Pet save(Pet pet);
    List<Pet> findAll();
    void deleteById(Long petId);
    List<Pet> findAllByUserId(UUID userId);
}
