package com.pets.application.device;

import com.pets.domain.repository.DeviceRepository;
import org.springframework.stereotype.Component;

@Component
public class ExistsDeviceByPetIdUseCase {
    private final DeviceRepository deviceRepository;

    public ExistsDeviceByPetIdUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public boolean execute(Long petId) {
        return deviceRepository.existsByPetId(petId);
    }
}

