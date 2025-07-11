package com.pets.application.device;

import com.pets.domain.repository.DeviceRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteDeviceUseCase {

    private final DeviceRepository deviceRepository;

    public DeleteDeviceUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void execute(String deviceId) {
        deviceRepository.deleteById(deviceId);
    }
}

