package com.pets.domain.records;

import com.pets.domain.model.Pet;
import com.pets.domain.model.UserRole;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        boolean isVerified,
        List<UserRole> roles,
        List<Pet> pets) { }