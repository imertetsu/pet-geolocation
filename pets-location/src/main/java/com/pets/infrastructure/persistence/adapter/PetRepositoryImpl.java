package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.model.Pet;
import com.pets.domain.repository.PetRepository;
import com.pets.infrastructure.mapper.PetMapper;
import com.pets.infrastructure.persistence.entities.PetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PetRepositoryImpl implements PetRepository {

    private final PetJpaRepository jpaRepository;

    @Override
    public Optional<Pet> findById(Long id) {
        return jpaRepository.findById(id)
                .map(PetMapper::toDomain);
    }

    @Override
    public Optional<Pet> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(PetMapper::toDomain);
    }

    @Override
    public Pet save(Pet pet) {
        PetEntity entity = PetMapper.toEntity(pet);
        PetEntity saved = jpaRepository.save(entity);
        return PetMapper.toDomain(saved);
    }

    @Override
    public List<Pet> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(PetMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pet> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream()
                .map(PetMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long petId) {
        jpaRepository.deleteById(petId);
    }
}

