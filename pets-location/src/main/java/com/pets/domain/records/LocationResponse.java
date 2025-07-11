package com.pets.domain.records;

import com.pets.domain.model.Location;

import java.time.LocalDateTime;

public record LocationResponse(
        Long id,
        Double latitude,
        Double longitude,
        LocalDateTime timestamp,
        Long petId
) {
    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getLatitude(),
                location.getLongitude(),
                location.getTimestamp(),
                location.getPetId()
        );
    }
}
