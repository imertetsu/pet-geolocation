package com.pets.domain.records;

import java.util.List;

public record UserRequest(
        String name,
        String email,
        String password,
        List<String> roles,
        String code
) {}