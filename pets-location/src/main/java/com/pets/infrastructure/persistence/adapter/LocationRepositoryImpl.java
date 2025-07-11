package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.model.Location;
import com.pets.domain.repository.LocationRepository;
import com.pets.infrastructure.mapper.LocationMapper;
import com.pets.infrastructure.persistence.entities.LocationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final LocationJpaRepository jpaRepository;

    @Override
    public Optional<Location> findById(Long id) {
        return jpaRepository.findById(id)
                .map(LocationMapper::toDomain);
    }

    @Override
    public Optional<Location> findByPetId(Long petId) {
        return jpaRepository.findFirstByPet_IdOrderByTimestampDesc(petId)
                .map(LocationMapper::toDomain);
    }

    @Override
    public Location save(Location location) {
        LocationEntity entity = LocationMapper.toEntity(location);
        LocationEntity saved = jpaRepository.save(entity);
        return LocationMapper.toDomain(saved);
    }

    @Override
    public List<Location> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(LocationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long locationId) {
        jpaRepository.deleteById(locationId);
    }
    @Override
    public Optional<Location> findLatestByPetId(Long petId) {
        return jpaRepository.findFirstByPetIdOrderByTimestampDesc(petId)
                .map(LocationMapper::toDomain);
    }

    @Override
    public List<Location> findAllByPetId(Long petId) {
        return jpaRepository.findAllByPetIdOrderByTimestampAsc(petId)
                .stream()
                .map(LocationMapper::toDomain)
                .collect(Collectors.toList());
    }
}
