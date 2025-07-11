package com.pets.domain.records;

import com.pets.domain.model.Pet;

import java.time.LocalDate;
import java.util.UUID;

public record PetResponse(
        Long id,
        String name,
        String species,
        String breed,
        LocalDate birthDate,
        UUID userId
) {
    public static PetResponse from(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getBirthDate(),
                pet.getUserId()
        );
    }
}
