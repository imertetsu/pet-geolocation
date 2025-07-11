package com.pets.infrastructure.config;

import com.pets.application.pet.DeletePetByIdUseCase;
import com.pets.application.pet.GetAllPetsUseCase;
import com.pets.application.pet.RegisterPetUseCase;
import com.pets.domain.repository.PetRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PetConfig {
    @Bean
    public RegisterPetUseCase registerPetUseCase(PetRepository petRepository){
        return new RegisterPetUseCase(petRepository);
    }
    @Bean
    public GetAllPetsUseCase getAllPetsUseCase(PetRepository petRepository){
        return new GetAllPetsUseCase(petRepository);
    }
    @Bean
    public DeletePetByIdUseCase deletePetByIdUseCase(PetRepository petRepository){
        return new DeletePetByIdUseCase(petRepository);
    }
}
