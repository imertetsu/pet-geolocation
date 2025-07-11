package com.pets.application.pet;

import com.pets.domain.model.Pet;
import com.pets.domain.repository.PetRepository;

public class RegisterPetUseCase {

    private final PetRepository petRepository;

    public RegisterPetUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet execute(Pet pet) {
        return petRepository.save(pet);
    }
}
