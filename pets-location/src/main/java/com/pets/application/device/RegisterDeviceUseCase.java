package com.pets.application.device;

import com.pets.domain.model.Device;
import com.pets.domain.repository.DeviceRepository;
import org.springframework.stereotype.Component;

@Component
public class RegisterDeviceUseCase {

    private final DeviceRepository deviceRepository;

    public RegisterDeviceUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void execute(Device device) {
        deviceRepository.save(device);
    }
}
