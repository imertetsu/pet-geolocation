package com.pets.domain.records;

import com.pets.domain.model.Pet;
import com.pets.domain.model.UserRole;
import com.pets.infrastructure.persistence.entities.AuthProvider;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        boolean isVerified,
        String photoUrl,
        AuthProvider provider,
        List<UserRole> roles,
        List<Pet> pets) { }