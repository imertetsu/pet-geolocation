package com.pets.infrastructure.mapper;

import com.pets.domain.model.Location;
import com.pets.infrastructure.persistence.entities.LocationEntity;
import com.pets.infrastructure.persistence.entities.PetEntity;

public class LocationMapper {

    public static Location toDomain(LocationEntity entity) {
        if (entity == null) return null;

        Location location = new Location();
        location.setId(entity.getId());
        location.setLatitude(entity.getLatitude());
        location.setLongitude(entity.getLongitude());
        location.setTimestamp(entity.getTimestamp());

        if (entity.getPet() != null) {
            location.setPetId(entity.getPet().getId());
        }

        return location;
    }

    public static LocationEntity toEntity(Location location) {
        if (location == null) return null;

        LocationEntity entity = new LocationEntity();
        entity.setId(location.getId());
        entity.setLatitude(location.getLatitude());
        entity.setLongitude(location.getLongitude());
        entity.setTimestamp(location.getTimestamp());

        PetEntity pet = new PetEntity();
        pet.setId(location.getPetId());
        entity.setPet(pet); // para setear relación sin cargar mascota entera

        return entity;
    }
}

