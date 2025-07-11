package com.pets.application.location;

import com.pets.domain.model.Location;
import com.pets.domain.repository.LocationRepository;

public class RegisterLocationUseCase {

    private final LocationRepository locationRepository;

    public RegisterLocationUseCase(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location execute(Location location) {
        return locationRepository.save(location);
    }
}
