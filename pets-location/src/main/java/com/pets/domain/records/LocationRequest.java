package com.pets.domain.records;

public record LocationRequest(
        Long petId,
        Double latitude,
        Double longitude
) {}
