package com.pets.application.pet;

import com.pets.domain.model.Pet;
import com.pets.domain.repository.PetRepository;

import java.util.List;

public class GetAllPetsUseCase {

    private final PetRepository petRepository;

    public GetAllPetsUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> execute() {
        return petRepository.findAll();
    }
}
