package com.pets.domain.records;

import java.time.LocalDate;
import java.util.UUID;

public record PetRequest(
        String name,
        String species,
        String breed,
        LocalDate birthDate,
        UUID userId
) {}
