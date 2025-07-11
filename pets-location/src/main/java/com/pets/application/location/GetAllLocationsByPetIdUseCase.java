package com.pets.application.location;

import com.pets.domain.model.Location;
import com.pets.domain.repository.LocationRepository;

import java.util.List;

public class GetAllLocationsByPetIdUseCase {

    private final LocationRepository locationRepository;

    public GetAllLocationsByPetIdUseCase(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> execute(Long petId) {
        return locationRepository.findAllByPetId(petId);
    }
}
