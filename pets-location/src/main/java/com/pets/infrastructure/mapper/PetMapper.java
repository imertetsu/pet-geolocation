package com.pets.infrastructure.mapper;

import com.pets.domain.model.Pet;
import com.pets.infrastructure.persistence.entities.PetEntity;
import com.pets.infrastructure.persistence.entities.UserEntity;

public class PetMapper {

    public static Pet toDomain(PetEntity entity) {
        if (entity == null) return null;

        Pet pet = new Pet();
        pet.setId(entity.getId());
        pet.setName(entity.getName());
        pet.setSpecies(entity.getSpecies());
        pet.setBreed(entity.getBreed());
        pet.setBirthDate(entity.getBirthDate());

        if (entity.getUser() != null) {
            pet.setUserId(entity.getUser().getId());
        }

        return pet;
    }

    public static PetEntity toEntity(Pet domain) {
        if (domain == null) return null;

        PetEntity entity = new PetEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSpecies(domain.getSpecies());
        entity.setBreed(domain.getBreed());
        entity.setBirthDate(domain.getBirthDate());

        if (domain.getUserId() != null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(domain.getUserId());
            entity.setUser(userEntity);
        }

        return entity;
    }
}
