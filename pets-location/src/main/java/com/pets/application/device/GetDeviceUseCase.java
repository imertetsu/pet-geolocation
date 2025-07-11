package com.pets.application.device;

import com.pets.domain.model.Device;
import com.pets.domain.repository.DeviceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetDeviceUseCase {

    private final DeviceRepository deviceRepository;

    public GetDeviceUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<Device> execute(String deviceId) {
        return deviceRepository.findById(deviceId);
    }
}

