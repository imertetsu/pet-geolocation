package com.pets.domain.records;

import com.pets.domain.model.Device;

public record DeviceResponse(
        String deviceId,
        Long petId
) {
    public static DeviceResponse from(Device device) {
        return new DeviceResponse(device.getId(), device.getPetId());
    }
}

