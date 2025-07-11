package com.pets.application.device;

import com.pets.domain.model.Device;
import com.pets.domain.repository.DeviceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllDevicesUseCase {

    private final DeviceRepository deviceRepository;

    public GetAllDevicesUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> execute() {
        return deviceRepository.findAll();
    }
}

