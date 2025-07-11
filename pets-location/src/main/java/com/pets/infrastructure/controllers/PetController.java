package com.pets.infrastructure.controllers;

import com.pets.application.pet.DeletePetByIdUseCase;
import com.pets.application.pet.GetAllPetsUseCase;
import com.pets.application.pet.GetPetsByUserIdUseCase;
import com.pets.application.pet.RegisterPetUseCase;
import com.pets.domain.model.Pet;
import com.pets.domain.records.PetRequest;
import com.pets.domain.records.PetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final RegisterPetUseCase registerPetUseCase;
    private final GetAllPetsUseCase getAllPetsUseCase;
    private final DeletePetByIdUseCase deletePetByIdUseCase;
    private final GetPetsByUserIdUseCase getPetsByUserIdUseCase;

    @PostMapping
    public ResponseEntity<PetResponse> register(@RequestBody PetRequest request) {
        Pet pet = new Pet(
                null,
                request.name(),
                request.species(),
                request.breed(),
                request.birthDate(),
                request.userId()
        );

        Pet saved = registerPetUseCase.execute(pet);
        return ResponseEntity.ok(PetResponse.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getAll() {
        List<Pet> pets = getAllPetsUseCase.execute();
        return ResponseEntity.ok(
                pets.stream().map(PetResponse::from).toList()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PetResponse>> getPetsByUser(@PathVariable UUID userId) {
        List<Pet> pets = getPetsByUserIdUseCase.execute(userId);

        List<PetResponse> responses = pets.stream()
                .map(PetResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        deletePetByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
