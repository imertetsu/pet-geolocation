package com.pets.application.device;

import com.pets.domain.model.Device;
import com.pets.domain.repository.DeviceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetDeviceIdByPetIdUseCase {
    private final DeviceRepository deviceRepository;

    public GetDeviceIdByPetIdUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<String> execute(Long petId) {
        return deviceRepository.findByPetId(petId)
                .map(Device::getId); // Devuelve el deviceId
    }
}

