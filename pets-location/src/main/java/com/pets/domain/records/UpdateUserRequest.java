package com.pets.domain.records;

public record UpdateUserRequest(
        String name,
        String password,
        String photoUrl
) {
}
