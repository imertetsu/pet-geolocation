package com.pets.application.location;

import com.pets.domain.model.Location;
import com.pets.domain.repository.LocationRepository;

import java.util.Optional;

public class GetLatestLocationByPetIdUseCase {

    private final LocationRepository locationRepository;

    public GetLatestLocationByPetIdUseCase(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Optional<Location> execute(Long petId) {
        return locationRepository.findLatestByPetId(petId);
    }
}
