package com.pets.application.pet;

import com.pets.domain.model.Pet;
import com.pets.domain.repository.PetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetPetsByUserIdUseCase {
    private final PetRepository petRepository;

    public GetPetsByUserIdUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }
    public List<Pet> execute(UUID userId) {
        return petRepository.findAllByUserId(userId);
    }
}
