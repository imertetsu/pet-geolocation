package com.pets.application.pet;

import com.pets.domain.repository.PetRepository;

public class DeletePetByIdUseCase {

    private final PetRepository petRepository;

    public DeletePetByIdUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public void execute(Long id) {
        petRepository.deleteById(id);
    }
}
