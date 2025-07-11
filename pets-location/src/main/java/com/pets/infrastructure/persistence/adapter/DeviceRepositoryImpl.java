package com.pets.infrastructure.persistence.adapter;

import com.pets.domain.model.Device;
import com.pets.domain.repository.DeviceRepository;
import com.pets.infrastructure.mapper.DeviceMapper;
import com.pets.infrastructure.persistence.entities.DeviceEntity;
import com.pets.infrastructure.persistence.entities.PetEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {

    private final DeviceJpaRepository jpaDeviceRepository;
    private final PetJpaRepository jpaPetRepository;

    public DeviceRepositoryImpl(DeviceJpaRepository jpaDeviceRepository, PetJpaRepository jpaPetRepository) {
        this.jpaDeviceRepository = jpaDeviceRepository;
        this.jpaPetRepository = jpaPetRepository;
    }

    @Override
    public Optional<Device> findById(String id) {
        return jpaDeviceRepository.findById(id)
                .map(DeviceMapper::toDomain);
    }

    @Override
    public void save(Device device) {
        PetEntity petEntity = jpaPetRepository.findById(device.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        DeviceEntity entity = DeviceMapper.toEntity(device, petEntity);
        jpaDeviceRepository.save(entity);
    }
    @Override
    public void deleteById(String id) {
        jpaDeviceRepository.deleteById(id);
    }
    @Override
    public List<Device> findAll() {
        return jpaDeviceRepository.findAll()
                .stream()
                .map(DeviceMapper::toDomain)
                .toList();
    }
    @Override
    public boolean existsByPetId(Long petId) {
        return jpaDeviceRepository.existsByPetId(petId);
    }

    @Override
    public Optional<Device> findByPetId(Long petId) {
        return jpaDeviceRepository.findByPetId(petId)
                .map(DeviceMapper::toDomain);
    }
}

