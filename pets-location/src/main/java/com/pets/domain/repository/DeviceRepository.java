package com.pets.domain.repository;

import com.pets.domain.model.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    Optional<Device> findById(String id);
    void save(Device device);
    void deleteById(String id);
    List<Device> findAll();
    boolean existsByPetId(Long petId);
    Optional<Device> findByPetId(Long petId);
}
